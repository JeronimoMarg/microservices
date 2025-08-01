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
public class ActualizacionStockDTO {
    
    private int id_orden;
    private Long id_producto;
    private Integer cantidad;

}
