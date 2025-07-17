package jeronimo.margitic.productos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jeronimo.margitic.productos.model.Categoria;
import jeronimo.margitic.productos.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;

    @GetMapping("/{categoriaId}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable(name="categoriaId") int id) {
        Optional<Categoria> categoriaBuscada = categoriaService.obtenerPorId(id);
        return ResponseEntity.of(categoriaBuscada);
    }
    
    @GetMapping("/todas")
    public ResponseEntity<List<Categoria>> obtenerCategorias(){
        List<Categoria> catObtenidas = categoriaService.obtenerTodas();
        return ResponseEntity.ok(catObtenidas);
    }

    @GetMapping("/nombre/{categoriaNombre}")
    public ResponseEntity<List<Categoria>> obtenerCategoriaPorNombre(@PathVariable(name="categoriaNombre") String nombre){
        List<Categoria> catObtenidas = categoriaService.obtenerPorNombre(nombre);
        return ResponseEntity.ok(catObtenidas);
    }

    @PostMapping(path = "/crear", consumes = "application/json")
    public ResponseEntity<Categoria> crearCategoria (@RequestBody Categoria categoriaNueva) {
        Categoria catCreada = categoriaService.crearCategoria(categoriaNueva);
        return ResponseEntity.status(201).body(catCreada);
    }

    @PutMapping(path="/{id}", consumes="application/json")
    public ResponseEntity<Categoria> modificarCategoria (@RequestBody Categoria categoriaAModificar, @PathVariable int id){
        Optional<Categoria> categoriaBuscada = categoriaService.obtenerPorId(id);
        if(categoriaBuscada.isPresent()){
            Categoria cat = categoriaService.modificarCategoria(categoriaAModificar);
            return ResponseEntity.ok(cat);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Categoria> eliminarCategoria (@PathVariable int id){
        Optional<Categoria> categoriaBuscada = categoriaService.obtenerPorId(id);
        if(categoriaBuscada.isPresent()){
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    
}
