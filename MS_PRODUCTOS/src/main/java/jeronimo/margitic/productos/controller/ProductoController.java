package jeronimo.margitic.productos.controller;

import java.util.Optional;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jeronimo.margitic.productos.model.Producto;
import jeronimo.margitic.productos.service.ProductoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<Producto> crearProducto (@RequestBody Producto productoNuevo) {
        Producto prodCreado = productoService.crearProducto(productoNuevo);
        return ResponseEntity.status(201).body(prodCreado);
    }

    @PutMapping(path="/{id}", consumes="application/json")
    public ResponseEntity<Producto> modificarProducto (@RequestBody Producto productoAModificar, @PathVariable int id){
        Optional<Producto> productoBuscado = productoService.obtenerPorId(id);
        if(productoBuscado.isPresent()){
            Producto prod = productoService.modificarProducto(productoAModificar);
            return ResponseEntity.ok(prod);
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
    
}
