package jeronimo.margitic.productos.service;
import jeronimo.margitic.productos.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jeronimo.margitic.productos.model.Categoria;
import jeronimo.margitic.productos.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    CategoriaService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    public List<Categoria> obtenerTodas(){
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtenerPorId(int id){
        return categoriaRepository.findById(id);
    }

    public List<Categoria> obtenerPorNombre(String nombre){
        return categoriaRepository.findByNombre(nombre);
    }

    public Categoria crearCategoria(Categoria cat){
        try{
            validarCategoria(cat);
            Categoria categoriaCreada = categoriaRepository.save(cat);
            return categoriaCreada;
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return cat;
    }

    public Categoria modificarCategoria(Categoria cat){
        return crearCategoria(cat);
    }

    public void eliminarCategoria(int id){
        categoriaRepository.deleteById(id);
    }

    private void validarCategoria(Categoria cat) throws IllegalArgumentException{
        if (cat.getNombre() == null){
            throw new IllegalArgumentException("La categoria debe tener un nombre.");
        }
    }


}
