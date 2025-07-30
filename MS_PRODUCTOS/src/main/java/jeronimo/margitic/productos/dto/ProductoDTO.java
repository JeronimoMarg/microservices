package jeronimo.margitic.productos.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ProductoDTO {

    //esta clase solamente se usa para recibir los productos de la cola.

    private Integer id_producto;
    private Integer cantidad;
    
}
