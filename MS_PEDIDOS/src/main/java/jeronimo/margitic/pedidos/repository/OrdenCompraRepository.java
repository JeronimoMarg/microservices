package jeronimo.margitic.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jeronimo.margitic.pedidos.model.OrdenCompra;
import java.util.Optional;


@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {

    Optional<OrdenCompra> findById(int id);

    Optional<OrdenCompra> findByNumeroPedido(int numero);
    
}
