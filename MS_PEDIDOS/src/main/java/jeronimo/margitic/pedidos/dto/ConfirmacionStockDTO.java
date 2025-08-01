package jeronimo.margitic.pedidos.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ConfirmacionStockDTO {

    private int id_orden;
    private Long id_producto;
    private Boolean actualizado;
    
}
