package jeronimo.margitic.pedidos.dto;

import java.util.List;

import jeronimo.margitic.pedidos.model.OrdenCompraDetalle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraDTO {
    
    private ClienteDTO cliente;
    private ObraDTO obra;
    private String observaciones;
    private List<OrdenCompraDetalle> detalles;

}
