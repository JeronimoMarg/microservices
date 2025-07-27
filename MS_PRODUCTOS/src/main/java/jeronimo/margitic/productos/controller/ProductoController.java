package jeronimo.margitic.productos.controller;

import java.util.Optional;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jeronimo.margitic.productos.model.Producto;
import jeronimo.margitic.productos.service.ProductoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class ProductoController {

    @Autowired
    ProductoService productoService;

    @GetMapping("/{productoId}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable(name="productoId") int id) {
        Optional<Producto> productoBuscado = productoService.obtenerPorId(id);
        return ResponseEntity.of(productoBuscado);
    }
    
    @GetMapping("/todos")
    public ResponseEntity<List<Producto>> obtenerProductos(){
        List<Producto> prodObtenidos = productoService.obtenerTodos();
        return ResponseEntity.ok(prodObtenidos);
    }

    @GetMapping("/nombre/{productoNombre}")
    public ResponseEntity<List<Producto>> obtenerProductoPorNombre(@PathVariable(name="productoNombre") String nombre){
        List<Producto> prodObtenidos = productoService.obtenerPorNombre(nombre);
        return ResponseEntity.ok(prodObtenidos);
    }

    @PostMapping(path = "/crear", consumes = "application/json")
    public ResponseEntity<Producto> crearProducto (@RequestBody Producto productoNuevo) {
        try{
            Producto prodCreado = productoService.crearProducto(productoNuevo);
            return ResponseEntity.status(201).body(prodCreado);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path="/{id}", consumes="application/json")
    public ResponseEntity<Producto> modificarProducto (@RequestBody Producto productoAModificar, @PathVariable int id){
        Optional<Producto> productoBuscado = productoService.obtenerPorId(id);
        if(productoBuscado.isPresent()){
            try{
                Producto prod = productoService.modificarProducto(productoAModificar);
                return ResponseEntity.ok(prod);
            }catch(Exception e){
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Producto> eliminarProducto (@PathVariable int id){
        Optional<Producto> productoBuscado = productoService.obtenerPorId(id);
        if(productoBuscado.isPresent()){
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path="/{id}/actualizarStockPrecio")
    public ResponseEntity<Producto> actualizarStockPrecio (@PathVariable int id, @RequestBody Map<String, Object> orden ){
        Optional<Producto> productoModificado = Optional.empty();

        if (orden.get("precio") != null && orden.get("cantidadStockRecibido") != null){
            Float precio = Float.valueOf(orden.get("precio").toString());
            Integer cantidadStockRecibido = Integer.valueOf(orden.get("cantidadStockRecibido").toString());
            try{
                productoModificado = productoService.actualizarStockYPrecio(id, precio, cantidadStockRecibido);
            }catch (Exception e){
                System.err.println("Error: " + e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }

        if(productoModificado.isPresent()){
            return ResponseEntity.ok(productoModificado.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path="/{id}/actualizarDescuento")
    public ResponseEntity<Producto> actualizarDescuento (@PathVariable int id, @RequestBody Map<String, Object> descuento){

        Optional<Producto> productoModificado = Optional.empty();

        if(descuento.get("descuento") != null){
            Float monto = Float.valueOf(descuento.get("descuento").toString());
            try{
                productoModificado = productoService.actualizarDescuento(id, monto);
            }catch (Exception e){
                System.err.println("Error: " + e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }

        if(productoModificado.isPresent()){
            return ResponseEntity.ok(productoModificado.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping(path="/actualizarStock/{id}")
    public ResponseEntity<Producto> actualizarStockProductos (@PathVariable int id, @RequestBody Map<String, Object> detalle){
        
        Optional<Producto> productoModificado = Optional.empty();

        if (detalle.get("id") != null && detalle.get("cantidad") != null){
            Integer idProducto = Integer.valueOf(detalle.get("id").toString());
            Integer cantidad = Integer.valueOf(detalle.get("cantidad").toString());
            try{
                productoModificado = productoService.actualizarStockProducto(idProducto, cantidad);
            }catch (Exception e){
                System.err.println("Error: " + e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }

        if(productoModificado.isPresent()){
            return ResponseEntity.ok(productoModificado.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }
    
}
