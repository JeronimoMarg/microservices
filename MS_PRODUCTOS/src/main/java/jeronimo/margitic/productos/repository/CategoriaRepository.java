package jeronimo.margitic.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jeronimo.margitic.productos.model.Categoria;
import java.util.List;
import java.util.Optional;


@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{

    Optional<Categoria> findById(int id);
    List<Categoria> findByNombre(String nombre);
    
}
