package jeronimo.margitic.pedidos.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    
    private int id_producto;
    private String nombre;
    private String descripcion;
    private float precio;
    private float descuento;
    private String categoria;

}
