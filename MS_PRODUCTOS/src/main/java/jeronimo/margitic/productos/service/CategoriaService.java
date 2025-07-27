package jeronimo.margitic.productos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jeronimo.margitic.productos.model.Categoria;
import jeronimo.margitic.productos.repository.CategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    //Obtiene todas las categorias
    public List<Categoria> obtenerTodas(){
        return categoriaRepository.findAll();
    }

    //Obtiene categoria segun id
    public Optional<Categoria> obtenerPorId(int id){
        return categoriaRepository.findById(id);
    }

    //Obtiene las categorias segun nombre
    public List<Categoria> obtenerPorNombre(String nombre){
        return categoriaRepository.findByNombre(nombre);
    }

    //Crear una categoria.
    public Categoria crearCategoria(Categoria cat) throws Exception{
        validarCategoria(cat);
        Categoria categoriaCreada = categoriaRepository.save(cat);
        return categoriaCreada;
    }

    //Modifica una categoria
    public Categoria modificarCategoria(Categoria cat) throws Exception{
        return crearCategoria(cat);
    }

    //Eliminar una categoria segun id.
    public void eliminarCategoria(int id){
        categoriaRepository.deleteById(id);
    }

    //Validacion de categoria. Solamente se verifica el nombre.
    private void validarCategoria(Categoria cat) throws IllegalArgumentException{
        if (cat.getNombre() == null){
            throw new IllegalArgumentException("La categoria debe tener un nombre.");
        }
    }


}
