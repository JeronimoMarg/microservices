package jeronimo.margitic.pedidos.dto;

import lombok.Data;

@Data
public class ObraDTO {

    private int id_obra;
    private String direccion;
    private String coordenadas;
    private int id_cliente;
    private float presupuestoEstimado;
    private String estadoObra;

}
