package jeronimo.margitic.pedidos.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jeronimo.margitic.pedidos.PedidosApplication;
import jeronimo.margitic.pedidos.dto.ClienteDTO;
import jeronimo.margitic.pedidos.dto.ObraDTO;
import jeronimo.margitic.pedidos.model.EstadoPedido;
import jeronimo.margitic.pedidos.model.HistorialEstado;
import jeronimo.margitic.pedidos.model.OrdenCompra;
import jeronimo.margitic.pedidos.model.OrdenCompraDetalle;
import jeronimo.margitic.pedidos.repository.OrdenCompraRepository;

@Service
public class OrdenCompraService {

    private final PedidosApplication pedidosApplication;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String url_clientes = "http:///cliente-service:8080/api/clientes";
    private final String url_productos = "http:///producto-service:8080/api/productos";

    OrdenCompraService(PedidosApplication pedidosApplication) {
        this.pedidosApplication = pedidosApplication;
    }

    public List<OrdenCompra> obtenerTodas(){
        return ordenCompraRepository.findAll();
    }

    public Optional<OrdenCompra> obtenerPedidoPorId(int id){
        return ordenCompraRepository.findById(id);
    }

    public Optional<OrdenCompra> obtenerPedidoPorNumero(int numero){
        return ordenCompraRepository.findByNumeroPedido(numero);
    }

    public OrdenCompra crearPedido(ClienteDTO cliente, ObraDTO obra, String observaciones, List<OrdenCompraDetalle> detalles){
        validarDatos(cliente, obra, detalles); 
        OrdenCompra nuevaOrden = new OrdenCompra(cliente, obra, observaciones, detalles);
        this.agregarEstadoHistorial(nuevaOrden, EstadoPedido.NUEVO);
        OrdenCompra savedOrden = ordenCompraRepository.save(nuevaOrden);
        return savedOrden;
    }

    public OrdenCompra crearPedidoCont(OrdenCompra orden){
        return actualizarPedido(aceptarPedido(orden)); 
    }

    public OrdenCompra preparacionPedido(OrdenCompra orden){
        return actualizarPedido(prepararPedido(orden));
    }
    
    public OrdenCompra actualizarPedido(OrdenCompra orden){
        // validar orden
        OrdenCompra ordenActualizada = ordenCompraRepository.save(orden);
        return ordenActualizada;
    }

    public void eliminarOrden(int id){
        ordenCompraRepository.deleteById(id);
    }

    private boolean validarDatos(ClienteDTO cliente, ObraDTO obra, List<OrdenCompraDetalle> detalles) {
        boolean val = false;
        try{
            validarCliente(cliente);
            validarObra(obra);
            validarDetalles(detalles);
            val = true;
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return val;
    }

    private void validarObra(ObraDTO obra) {
        // TODO Auto-generated method stub
    }

    private void validarDetalles(List<OrdenCompraDetalle> detalles) {
        // TODO Auto-generated method stub
    }

    private void validarCliente(ClienteDTO cliente) {
        // TODO Auto-generated method stub
    }

    private void agregarEstadoHistorial(OrdenCompra orden, EstadoPedido estado){
        HistorialEstado hist = new HistorialEstado();
        hist.setFechaEstado(Instant.now());
        //hist.userEstado = ...
        //hist.detalle = ...
        orden.getEstados().add(hist);
    }

    private boolean verificarCreditoCliente(OrdenCompra orden){
        ResponseEntity<Boolean> respuesta = consultaRESTCreditoCliente(orden);
        if (respuesta.getBody() != null){
            return respuesta.getBody();
        }else{
            return false;
        }
    }

    private ResponseEntity<Boolean> consultaRESTCreditoCliente(OrdenCompra orden){
        int clienteId = orden.getCliente().getId_cliente();
        String url = url_clientes + "/verificarSaldo/" + String.valueOf(clienteId);

        Map<String, Object> totalOrden = new HashMap<>();
        totalOrden.put("total", orden.getTotal());

        ResponseEntity<Boolean> respuesta = restTemplate.postForEntity(url, totalOrden, Boolean.class);
        return respuesta;
    }

    private OrdenCompra aceptarPedido (OrdenCompra orden) {
        EstadoPedido nuevoEstado;
        if(verificarCreditoCliente(orden)){
            nuevoEstado = EstadoPedido.ACEPTADO;
        }else{
            nuevoEstado = EstadoPedido.RECHAZADO;
        }
        orden.setEstado(nuevoEstado);
        agregarEstadoHistorial(orden, nuevoEstado);
        return orden;
    }

    private boolean actualizarStockProductos (OrdenCompra orden) {
        HashMap<Integer, Integer> cantidades = new HashMap<>(); 
        boolean retorno = true;
        //obtener los detalles de la orden de compra
        orden.getDetalles()
        .stream()
        .forEach(d -> cantidades.put(d.getProducto().getId_producto(), d.getCantidad()));
        //consultar via rest api todos los detalles
        for(Map.Entry<Integer, Integer> entry : cantidades.entrySet()){
            // entry.getKey() is the product ID, entry.getValue() is the quantity
            boolean aux = consultaRESTStockProductos(entry.getKey(), entry.getValue()).getBody();
            retorno = retorno && aux;
        }
        return retorno;
    }

    private ResponseEntity<Boolean> consultaRESTStockProductos (Integer id, Integer cantidad){

        String url = url_productos + "/actualizarStock" + String.valueOf(id);

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("cantidad", cantidad);

        ResponseEntity<Boolean> respuesta = restTemplate.postForEntity(url, body, Boolean.class);
        return respuesta;
        
    }
    
    private OrdenCompra prepararPedido (OrdenCompra orden){
        EstadoPedido nuevoEstado;
        if(actualizarStockProductos(orden)){
            nuevoEstado = EstadoPedido.EN_PREPARACION;
            orden.setEstado(nuevoEstado);
            agregarEstadoHistorial(orden, nuevoEstado);
        }
        return orden;
    }

    public List<OrdenCompra> obtenerPedidosActivosPorCliente (int id_cliente){
        
        List<OrdenCompra> retorno = this.obtenerTodas()
        .stream()
        .filter(o -> o.getCliente().getId_cliente() == id_cliente)
        .filter(o -> o.getEstado() != EstadoPedido.RECHAZADO && o.getEstado() != EstadoPedido.ENTREGADO)
        .collect(Collectors.toList());

        return retorno;

    }

}
