package jeronimo.margitic.pedidos.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jeronimo.margitic.pedidos.dto.ClienteDTO;
import jeronimo.margitic.pedidos.dto.ObraDTO;
import jeronimo.margitic.pedidos.dto.OrdenCompraDTO;
import jeronimo.margitic.pedidos.exception.SaldoInsuficienteException;
import jeronimo.margitic.pedidos.exception.StockInsuficienteException;
import jeronimo.margitic.pedidos.model.EstadoPedido;
import jeronimo.margitic.pedidos.model.HistorialEstado;
import jeronimo.margitic.pedidos.model.OrdenCompra;
import jeronimo.margitic.pedidos.model.OrdenCompraDetalle;
import jeronimo.margitic.pedidos.repository.OrdenCompraRepository;

@Service
public class OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private RestTemplate restTemplate;

    SenderService sender = new SenderService("rabbitmq", 5672, "admin", "secret");

    private final String url_clientes = "http:///cliente-service:8080/api/clientes";
    private final String url_productos = "http:///producto-service:8080/api/productos";

    //Obtiene todos los pedidos.
    public List<OrdenCompra> obtenerTodas(){
        return ordenCompraRepository.findAll();
    }

    //Obtiene un pedido segun id.
    public Optional<OrdenCompra> obtenerPedidoPorId(int id){
        return ordenCompraRepository.findById(id);
    }

    //Obtiene un pedido segun el numero.
    public Optional<OrdenCompra> obtenerPedidoPorNumero(int numero){
        return ordenCompraRepository.findByNumeroPedido(numero);
    }

    //Crea un pedido.
    public OrdenCompra crearPedido(OrdenCompraDTO orden) throws Exception{
        ClienteDTO cliente = orden.getCliente();
        ObraDTO obra = orden.getObra();
        String observaciones = orden.getObservaciones();
        List<OrdenCompraDetalle> detalles = orden.getDetalles();
        //Se crea el objeto orden a partir de los DTOs
        OrdenCompra nuevaOrden = new OrdenCompra(cliente, obra, observaciones, detalles);
        //Se validan los datos
        validarDatos(cliente, nuevaOrden, detalles, obra); 
        //Se agrega el primer estado al historial: NUEVO
        this.agregarEstadoHistorial(nuevaOrden, EstadoPedido.NUEVO);
        //Se guarda la orden
        OrdenCompra savedOrden = ordenCompraRepository.save(nuevaOrden);
        return savedOrden;
    }

    //Los siguientes metodos cambian el estado de un pedido y los guardan en la BD.
    public OrdenCompra crearPedidoYActualizar(OrdenCompra orden) throws Exception{
        return actualizarPedido(aceptarPedido(orden)); 
    }
    
    public OrdenCompra aceptarPedidoYActualizar(OrdenCompra orden) throws Exception{
        try{
            return actualizarPedido(aceptarPedido(orden));
        }catch(SaldoInsuficienteException e){
            actualizarPedido(rechazarPedido(orden));
            throw(e);
        }
    }

    public OrdenCompra prepararPedidoYActualizar(OrdenCompra orden) throws Exception{
        try{
            return actualizarPedido(prepararPedido(orden));
        }catch(StockInsuficienteException e){
            //El pedido queda en estado ACEPTADO
            throw(e);
        }
    }

    public OrdenCompra entregarPedidoYActualizar(OrdenCompra orden) throws Exception{
        return actualizarPedido(entregarPedido(orden));
    }

    public OrdenCompra cancelarPedidoYActualizar(OrdenCompra orden) throws Exception{
        //Si se cancela el pedido entonces devolver el stock de todos los productos --> rabbit
        OrdenCompra cancelada = actualizarPedido(cancelarPedido(orden));
        //Devolver todo el stock de productos
        List<Map<String,Object>> listaProductos = new ArrayList<>();
        //Se manda una lista de objetos con claves: id, cantidad
        for(OrdenCompraDetalle o: orden.getDetalles()){
            Map<String,Object> item = new HashMap<>();
            item.put("id_producto", o.getProducto().getId_producto());
            item.put("cantidad",o.getCantidad());
            listaProductos.add(item);
        }
        System.out.println("Productos cancelados: " + listaProductos);
        sender.sendList("cola_cancelacion", listaProductos);
        return cancelada;
    }

    //Se actualiza un pedido.
    public OrdenCompra actualizarPedido(OrdenCompra orden) throws Exception{
        // Validar orden
        validarDatos(orden.getCliente(), orden, orden.getDetalles(), orden.getObra()); 
        OrdenCompra ordenActualizada = ordenCompraRepository.save(orden);
        return ordenActualizada;
    }

    //Elimina una orden segun id.
    public void eliminarOrden(int id){
        ordenCompraRepository.deleteById(id);
    }

    //Se validan los datos de una orden
    private boolean validarDatos(ClienteDTO cliente, OrdenCompra orden, List<OrdenCompraDetalle> detalles, ObraDTO obra) throws Exception{
        boolean val = false;
        try{
            validarCliente(cliente);
            validarOrden(orden);
            validarDetalles(detalles);
            validarObra(obra);
            val = true;
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            throw(e);
        }
        return val;
    }

    //Validar los datos de orden.
    private void validarOrden(OrdenCompra orden) throws IllegalArgumentException{
        if(orden.getFecha().isAfter(Instant.now()) 
        || orden.getTotal() < 0){
            throw new IllegalArgumentException("La fecha o el total de la obra son invalidos.");
        }
    }

    //Ahora, se validan los detalles de la orden
    private void validarDetalles(List<OrdenCompraDetalle> detalles) throws IllegalArgumentException{
        for(OrdenCompraDetalle detalle: detalles){
            if(detalle.getProducto() == null 
            || detalle.getCantidad() <= 0 
            || detalle.getTotal() < 0){
                throw new IllegalArgumentException("El/los detalles son invalidos. Revisar el producto, cantidad y total.");
            }
        }
    }

    //Validan los datos del cliente.
    private void validarCliente(ClienteDTO cliente) throws IllegalArgumentException{
        if (cliente.getNombre() == null 
        || cliente.getApellido() == null 
        || cliente.getDni() <= 0
        || cliente.getFechaNacimiento() == null
        || cliente.getCorreoElectronico() == null){
            throw new IllegalArgumentException("Los datos del cliente son invalidos.");
        }
    }

    //Validan los datos de la obra.
    private void validarObra(ObraDTO obra) throws IllegalArgumentException{
        if (obra.getDireccion() == null
        || obra.getCoordenadas() == null
        || obra.getId_cliente() <= 0
        || obra.getPresupuestoEstimado() <= 0
        || obra.getEstadoObra() == null){
            throw new IllegalArgumentException("Los datos de obra son invalidos.");
        }
    }

    //Se agrega al historial de una orden un estado.
    private OrdenCompra agregarEstadoHistorial(OrdenCompra orden, EstadoPedido estado){
        HistorialEstado hist = new HistorialEstado();
        hist.setFechaEstado(Instant.now());
        //hist.userEstado = ...
        //hist.detalle = ...
        orden.getEstados().add(hist);
        return orden;
    }

    //Verifica el credito del cliente para un pedido.
    private boolean verificarCreditoCliente(OrdenCompra orden){
        //Se consulta directamente al servicio de clientes.
        ResponseEntity<Boolean> respuesta = consultaRESTCreditoCliente(orden);
        if (respuesta.getBody() != null){
            return respuesta.getBody();
        }else{
            return false;
        }
    }

    //Se realiza la consulta rest al servicio de clientes.
    private ResponseEntity<Boolean> consultaRESTCreditoCliente(OrdenCompra orden){
        int clienteId = orden.getCliente().getId_cliente();
        String url = url_clientes + "/verificarSaldo/" + String.valueOf(clienteId);

        /* El body es un objeto JSON con la siguiente forma:
         *
         * total: 123.45
         */
        Map<String, Object> totalOrden = new HashMap<>();
        totalOrden.put("total", orden.getTotal());

        ResponseEntity<Boolean> respuesta = restTemplate.postForEntity(url, totalOrden, Boolean.class);
        return respuesta;
    }

    //Acepta un pedido
    private OrdenCompra aceptarPedido (OrdenCompra orden) throws SaldoInsuficienteException{
        EstadoPedido nuevoEstado;
        //primero consulta via REST al servicio de clientes. Si el credito del cliente alcanza, entonces se agrega un nuevo estado.
        if(verificarCreditoCliente(orden)){
            nuevoEstado = EstadoPedido.ACEPTADO;
            orden.setEstado(nuevoEstado);
            //Se guarda en el historial de estados
            agregarEstadoHistorial(orden, nuevoEstado);
        }else{
            throw new SaldoInsuficienteException("El saldo del cliente no es suficiente para el pedido");
        }
        return orden;
    }

    //Actualiza el stock de productos para un pedido.
    private Map<Integer,Boolean> actualizarStockProductos (OrdenCompra orden){
        HashMap<Integer, Integer> cantidades = new HashMap<>(); 
        //obtener los detalles de la orden de compra
        //Se arma un objeto JSON con la forma
        /*
         * [id] : [cantidad]
         * 1: 100
         * 2: 200
         */
        orden.getDetalles()
        .stream()
        .forEach(d -> cantidades.put(d.getProducto().getId_producto(), d.getCantidad()));

        //armar retorno
        //mapa integer , boolean
        //integer = id producto
        //boolean = si pudo actualizarce el stock normalmente
        Map<Integer,Boolean> retorno = new HashMap<>();

        //consultar via rest api todos los detalles
        for(Map.Entry<Integer, Integer> entry : cantidades.entrySet()){
            // entry.getKey() is the product ID, entry.getValue() is the quantity
            boolean respuesta = consultaRESTStockProductos(entry.getKey(), entry.getValue());

            //Se agrega un elemento al mapa
            retorno.put(entry.getKey(), respuesta);

            // Si hay stock, el servicio de productos ya lo actualizó
        }
        return retorno;
    }

    //Se hace la consulta REST al servicio de productos.
    private boolean consultaRESTStockProductos (Integer id, Integer cantidad){

        String url = url_productos + "/actualizarStock" + String.valueOf(id);

        //Se hace un objeto JSON con la siguiente forma
        /*
         * "id":1,
         * "cantidad":100
         */
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("cantidad", cantidad);

        ResponseEntity<Map> respuesta = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.PUT,
            new HttpEntity<>(body),
            Map.class
        );
        // Si el producto se actualizó, hay stock suficiente
        return respuesta.getStatusCode().is2xxSuccessful();
        
    }
    
    //Se prepara un pedido
    private OrdenCompra prepararPedido (OrdenCompra orden) throws StockInsuficienteException{
        EstadoPedido nuevoEstado;
        Map<Integer,Boolean> actualizaciones = actualizarStockProductos(orden);
        Map<Integer,Boolean> mapaFalso = actualizaciones.entrySet().stream().filter(e -> !e.getValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if(mapaFalso.size() == 0){
            nuevoEstado = EstadoPedido.EN_PREPARACION;
            orden.setEstado(nuevoEstado);
            agregarEstadoHistorial(orden, nuevoEstado);
        }else{
            Set<Integer> ids = mapaFalso.keySet();
            // devolver stock ... rabbit ?
            // o simplemente volver a descontar stock con otro metodo dentro de un tiempo ...
            throw new StockInsuficienteException("No hay stock para algunos de los productos indicados: " + ids);
        }
        return orden;
    }

    //Se obtienen todos los pedidos activos de un cliente
    public List<OrdenCompra> obtenerPedidosActivosPorCliente (int id_cliente){
        
        List<OrdenCompra> retorno = this.obtenerTodas()
        .stream()
        .filter(o -> o.getCliente().getId_cliente() == id_cliente)
        .filter(o -> o.getEstado() != EstadoPedido.RECHAZADO && o.getEstado() != EstadoPedido.ENTREGADO)
        .collect(Collectors.toList());

        return retorno;

    }

    //Se entrega un pedido. Agregando nuevo estado e historial.
    private OrdenCompra entregarPedido (OrdenCompra orden){
        EstadoPedido nuevoEstado;
        nuevoEstado = EstadoPedido.ENTREGADO;
        orden.setEstado(nuevoEstado);
        agregarEstadoHistorial(orden, nuevoEstado);
        return orden;
    }

    //Se cancela un pedido. Agregando nuevo estado e historial.
    private OrdenCompra cancelarPedido (OrdenCompra orden){
        EstadoPedido nuevoEstado;
        nuevoEstado = EstadoPedido.CANCELADO;
        orden.setEstado(nuevoEstado);
        agregarEstadoHistorial(orden, nuevoEstado);
        return orden;
    }

    //Se rechaza un pedido (saldo insuficiente)
    private OrdenCompra rechazarPedido (OrdenCompra orden){
        EstadoPedido nuevoEstado = EstadoPedido.RECHAZADO;
        orden.setEstado(nuevoEstado);
        agregarEstadoHistorial(orden, nuevoEstado);
        return orden;
    }

}
