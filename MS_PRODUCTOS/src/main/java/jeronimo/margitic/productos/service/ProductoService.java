package jeronimo.margitic.productos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jeronimo.margitic.productos.dto.ConfirmacionStockDTO;
import jeronimo.margitic.productos.dto.ProductoDTO;
import jeronimo.margitic.productos.exception.StockInsuficienteException;
import jeronimo.margitic.productos.model.Producto;
import jeronimo.margitic.productos.repository.ProductoRepository;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    SenderService sender = new SenderService("rabbitmq", 5672, "admin", "secret");

    //Obtiene todos los productos.
    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    //Obtiene un producto segun id.
    public Optional<Producto> obtenerPorId(Long id){
        return productoRepository.findById(id);
    }

    //Obtiene un producto segun nombre.
    public List<Producto> obtenerPorNombre(String nombre){
        return productoRepository.findByNombre(nombre);
    }

    //Crea un nuevo producto.
    public Producto crearProducto(Producto prod) throws Exception{
        //El descuento promocional es 0
        //El stock inicial es 0
        //Eso deberia venir ya seteado desde el front.
        validarProducto(prod);
        Producto productoCreado = productoRepository.save(prod);
        return productoCreado;
    }

    //Modifica un producto.
    public Producto modificarProducto(Producto prod) throws Exception{
        return crearProducto(prod);
    }

    //Modifica un producto pero no pasa por la validacion
    public Producto modificarProductoSinValidar(Producto prod) {
        return productoRepository.save(prod);
    }

    //Elimina un produto segun id.
    public void eliminarProducto(Long id){
        productoRepository.deleteById(id);
    }

    //Valida un producto, es decir, la descripcion y el precio
    private void validarProducto(Producto prod) throws IllegalArgumentException{
        if(prod.getNombre() == null && prod.getDescripcion() == null && prod.getPrecio() <= 0 && prod.getCategoria() == null && prod.getDescuentoPromocional() <= 0){
            throw new IllegalArgumentException("El producto es invalido. Verificar nombre, descripcion, precio, descuento y categoria");
        }
    }

    //Metodo que actualiza stock y precio de un producto segun id.
    //Esto se utiliza cuando ingresa stock a la casa central.
    public Optional<Producto> actualizarStockYPrecio(Long id, Float precio, Integer cantidadStockRecibido) throws Exception{
        
        Optional<Producto> producto = this.obtenerPorId(id);
        if(producto.isPresent() && precio >= 0 && cantidadStockRecibido >= 0){
            //Se actualiza el stock y el precio
            producto.get().actualizarStockRecibido(cantidadStockRecibido);
            producto.get().setPrecio(precio);
            //Se guarda en la BD.
            Producto modificado = this.modificarProducto(producto.get());
            return Optional.ofNullable(modificado);
        }
        return Optional.empty();

    }

    //Actualiza el descuento promocional de un producto.
    public Optional<Producto> actualizarDescuento(Long id, Float monto) throws Exception {
        
        Optional<Producto> producto = this.obtenerPorId(id);
        if(producto.isPresent() && monto >= 0){
            //Se actualiza el descuento.
            producto.get().setDescuentoPromocional(monto);
            //Se guarda en la BD.
            Producto modificado = this.modificarProducto(producto.get());
            return Optional.ofNullable(modificado);
        }
        return Optional.empty();
    }

    //Metodo que actualiza stock.
    //Esto se utiliza cuando alguien compra, por ende descuenta unidades del stock.
    public Optional<Producto> actualizarStockProducto(Long idProducto, Integer cantidad) throws Exception {
        
        Optional<Producto> producto = this.obtenerPorId(idProducto);
        if(producto.isPresent() && cantidad > 0){
            //Primero se verifica que exista suficiente stock disponible 
            Boolean esPosible = producto.get().verificarStockPedido(cantidad);
            if (esPosible){
                //Solamente modificamos el producto si se tiene stock
                Producto modificado = producto.get().actualizarStockPedido(cantidad); 
                modificado = this.modificarProducto(modificado);
                return Optional.ofNullable(modificado);
            } /* else{
                throw new StockInsuficienteException("No hay stock suficiente para el producto con id: " + String.valueOf(idProducto));
            } */
        }
        return Optional.empty();
    }

    //Metodo llamado cuando se escuchan mensajes de la cola rabbit
    //Devuelve el stock de todos los productos.
    public List<Producto> reponerStock(List<ProductoDTO> productos){
        List<Producto> actualizados = new ArrayList<>();
        for (ProductoDTO p : productos){
            Optional<Producto> buscado = this.obtenerPorId(p.getId_producto());
            if(buscado.isPresent()){
                buscado.get().actualizarStockRecibido(p.getCantidad());
                Producto aux = this.modificarProductoSinValidar(buscado.get());
                actualizados.add(aux);
            }
        }
        return actualizados;
    }

    //Metodo que recibe la lista de DTOs de la cola de rabbit.
    //Llama a actualizarStockProductos para cada uno de ellos.
    public List<ConfirmacionStockDTO> actualizarStockListaProductos(List<ProductoDTO> productos) throws Exception {

        List<ConfirmacionStockDTO> confirmaciones = new ArrayList<>();

        //Se actualiza el stock de cada uno de los productos.
        for(ProductoDTO prod: productos){
            Optional<Producto> aux = actualizarStockProducto(prod.getId_producto(), prod.getCantidad());
            ConfirmacionStockDTO confirmacion = new ConfirmacionStockDTO();
            confirmacion.setId_orden(prod.getId_orden());
            confirmacion.setId_producto(prod.getId_producto());
            confirmacion.setActualizado(aux.isPresent());
            confirmaciones.add(confirmacion);
        }
        
        return confirmaciones;

    }

    public void mandarConfirmaciones(List<ConfirmacionStockDTO> lista) {
        sender.sendList("cola_confirmacion", lista);
    }

}
