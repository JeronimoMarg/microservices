package jeronimo.margitic.pedidos.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClienteDTO {
    
    //informacion basica (datos) del cliente solamente

    private int id_cliente;
    private String nombre;
    private String apellido;
    private long dni;
    private LocalDate fechaNacimiento;
    private String calleDomicilio;
    private String numeroDomicilio;
    private String numeroTelefono;
    private String correoElectronico;

}
