package jeronimo.margitic.pedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jeronimo.margitic.pedidos.dto.ProductoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table
public class OrdenCompraDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id_orden_detalle")
    private int id;
    @Embedded
    private ProductoDTO producto;
    private int cantidad;
    private float descuento;
    private float total;

}
