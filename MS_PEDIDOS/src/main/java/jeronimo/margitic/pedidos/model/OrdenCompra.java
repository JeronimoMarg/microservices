package jeronimo.margitic.pedidos.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jeronimo.margitic.pedidos.dto.ClienteDTO;
import jeronimo.margitic.pedidos.dto.ObraDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"numeroPedido"})})
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id_orden")
    private int id;
    @Column(columnDefinition="TIMESTAMP")
    private Instant fecha;
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int numeroPedido;
    private String user;
    private String observaciones;
    @Embedded
    private ClienteDTO cliente;
    @Embedded
    private ObraDTO obra;
    @OneToMany
    @JoinColumn(name = "id_orden")
    private List<HistorialEstado> estados;
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;
    private double total;
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="id_orden")
    private List<OrdenCompraDetalle> detalles;

    public OrdenCompra (ClienteDTO cliente, ObraDTO obra, String observaciones, List<OrdenCompraDetalle> detalles) {
        OrdenCompra orden = new OrdenCompra();
        orden.fecha = Instant.now();
        // orden.user = ...
        orden.observaciones = observaciones;
        orden.cliente = cliente;
        orden.obra = obra;
        orden.estados = new ArrayList<HistorialEstado>();
        orden.estado = EstadoPedido.NUEVO;
        orden.total = obtenerTotal(detalles);
        orden.detalles = detalles;
    }

    private double obtenerTotal(List<OrdenCompraDetalle> detalles) {
        return detalles.stream().mapToDouble(d -> d.getTotal()).sum();
    }
    
}
