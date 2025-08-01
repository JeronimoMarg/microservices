package jeronimo.margitic.productos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionStockDTO {

    private int id_orden;
    private Long id_producto;
    private Boolean actualizado;
    
}
