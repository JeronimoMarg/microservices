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

    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(int id){
        return productoRepository.findById(id);
    }

    public List<Producto> obtenerPorNombre(String nombre){
        return productoRepository.findByNombre(nombre);
    }

    public Producto crearProducto(Producto prod){
        //El descuento promocional es 0
        //El stock inicial es 0
        try{
            validarProducto(prod);
            Producto productoCreado = productoRepository.save(prod);
            return productoCreado;
        } catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return prod;
    }

    public Producto modificarProducto(Producto prod){
        //Hace lo mismo que guardar producto
        return crearProducto(prod);
    }

    public void eliminarProducto(int idProd){
        productoRepository.deleteById(idProd);
    }

    private void validarProducto(Producto prod) throws IllegalArgumentException{
        if(prod.getNombre() == null && prod.getDescripcion() == null && prod.getPrecio() <= 0 && prod.getCategoria() == null){
            throw new IllegalArgumentException("El producto es invalido. Verificar nombre, descripcion, precio y categoria");
        }
    }

    public Optional<Producto> actualizarStockYPrecio(int id, Float precio, Integer cantidadStockRecibido) {
        
        Optional<Producto> producto = this.obtenerPorId(id);
        if(producto.isPresent()){
            producto.get().actualizarStock(cantidadStockRecibido);
            producto.get().setPrecio(precio);
            Producto modificado = this.modificarProducto(producto.get());
            return Optional.ofNullable(modificado);
        }
        return Optional.empty();

    }

    public Optional<Producto> actualizarDescuento(int id, Float monto) {
        
        Optional<Producto> producto = this.obtenerPorId(id);
        if(producto.isPresent()){
            producto.get().setDescuentoPromocional(monto);
            Producto modificado = this.modificarProducto(producto.get());
            return Optional.ofNullable(modificado);
        }
        return Optional.empty();
    }

}
