package jeronimo.margitic.productos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jeronimo.margitic.productos.model.Producto;
import jeronimo.margitic.productos.repository.ProductoRepository;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    //Obtiene todos los productos.
    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    //Obtiene un producto segun id.
    public Optional<Producto> obtenerPorId(int id){
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

    //Elimina un produto segun id.
    public void eliminarProducto(int idProd){
        productoRepository.deleteById(idProd);
    }

    //Valida un producto, es decir, la descripcion y el precio
    private void validarProducto(Producto prod) throws IllegalArgumentException{
        if(prod.getNombre() == null && prod.getDescripcion() == null && prod.getPrecio() <= 0 && prod.getCategoria() == null && prod.getDescuentoPromocional() <= 0){
            throw new IllegalArgumentException("El producto es invalido. Verificar nombre, descripcion, precio, descuento y categoria");
        }
    }

    //Metodo que actualiza stock y precio de un producto segun id.
    //Esto se utiliza cuando ingresa stock a la casa central.
    public Optional<Producto> actualizarStockYPrecio(int id, Float precio, Integer cantidadStockRecibido) throws Exception{
        
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
    public Optional<Producto> actualizarDescuento(int id, Float monto) throws Exception {
        
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
    public Optional<Producto> actualizarStockProducto(Integer idProducto, Integer cantidad) throws Exception {
        
        Optional<Producto> producto = this.obtenerPorId(idProducto);
        if(producto.isPresent() && cantidad > 0){
            //Primero se verifica que exista suficiente stock disponible 
            Boolean esPosible = producto.get().actualizarStockPedido(cantidad);
            if (esPosible){
                //Solamente modificamos el producto si se tiene stock
                Producto modificado = this.modificarProducto(producto.get());
                return Optional.ofNullable(modificado);
            }else{
                //Meter alguna logica en el caso de que no haya stock
            }
            return producto;
        }
        return Optional.empty();
    }

}
