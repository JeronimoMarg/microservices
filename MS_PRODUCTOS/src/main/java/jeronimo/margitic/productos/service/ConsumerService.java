package jeronimo.margitic.productos.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import jeronimo.margitic.productos.dto.ConfirmacionStockDTO;
import jeronimo.margitic.productos.dto.ProductoDTO;
import jeronimo.margitic.productos.exception.StockInsuficienteException;
import jeronimo.margitic.productos.model.Producto;

import java.util.List;

@Component
public class ConsumerService {
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductoService productoService = new ProductoService();

    @RabbitListener(queues = "cola_cancelacion")
    public void recibirMensajeCancelacion(String mensajeJson) {
        try {
            List<ProductoDTO> productos = objectMapper.readValue(
                mensajeJson, new TypeReference<List<ProductoDTO>>() {}
            );

            List<Producto> lista = productoService.reponerStock(productos);

            System.out.println("Productos Actualizados: " + lista);

        } catch (JsonProcessingException e) {
            System.err.println("Error deserializando el mensaje: {}" + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error procesando el mensaje: {}" + e.getMessage());
            e.printStackTrace();
      }
    }

    @RabbitListener(queues = "cola_preparacion")
    public void recibirMensajePreparacion(String mensajeJson) {
        try {
            List<ProductoDTO> productos = objectMapper.readValue(
                mensajeJson, new TypeReference<List<ProductoDTO>>() {}
            );
            
            List<ConfirmacionStockDTO> lista = productoService.actualizarStockListaProductos(productos);

            //Mandar a la cola de de confirmacion
            productoService.mandarConfirmaciones(lista);

            System.out.println("Productos Actualizados: " + lista);

        } catch (JsonProcessingException e) {
            System.err.println("Error deserializando el mensaje: {}" + e.getMessage());
        } catch (StockInsuficienteException e){
            System.err.println("Error con el stock del producto: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error procesando el mensaje: {}" + e.getMessage());
            e.printStackTrace();
      }
    }

}
