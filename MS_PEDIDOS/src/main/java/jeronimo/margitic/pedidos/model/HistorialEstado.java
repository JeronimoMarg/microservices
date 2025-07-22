package jeronimo.margitic.pedidos.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class HistorialEstado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id_historial")
    private int id;
    private EstadoPedido estado;
    private Instant fechaEstado;
    private String userEstado;
    private String detalle;

}
