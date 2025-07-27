package jeronimo.margitic.pedidos.dto;

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
public class ObraDTO {

    private int id_obra;
    private String direccion;
    private String coordenadas;
    private int id_cliente;
    private float presupuestoEstimado;
    private String estadoObra;

}
