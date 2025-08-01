package jeronimo.margitic.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jeronimo.margitic.productos.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{
    
    Optional<Producto> findById(Long idProducto);
    List<Producto> findByNombre(String nombre);
    void deleteById(Long id);
    
}
