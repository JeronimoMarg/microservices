package jeronimo.margitic.productos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
@Table()
public class Producto {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id_producto")
    private int id;
    private String nombre;
    @Column(length = 20)
    private String descripcion;
    private int stockActual;
    private int stockMinimo;
    private float precio;
    @OneToOne
    @JoinColumn(name="id_categoria")
    private Categoria categoria;
    private float descuentoPromocional;

    public void actualizarStock(int stock){
        this.stockActual = this.stockActual + stock;
    }

}
