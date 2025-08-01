package jeronimo.margitic.pedidos.service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jeronimo.margitic.pedidos.dto.ConfirmacionStockDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;


@Component
public class ConsumerService {
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OrdenCompraService ordenCompraService = new OrdenCompraService();

    @RabbitListener(queues = "cola_confirmacion")
    public void recibirMensaje(String mensajeJson) {
        try {
            List<ConfirmacionStockDTO> productos = objectMapper.readValue(
                mensajeJson, new TypeReference<List<ConfirmacionStockDTO>>() {}
            );

            //logica
            ordenCompraService.devolucionPreparacionPedido(productos);

        } catch (JsonProcessingException e) {
            System.err.println("Error deserializando el mensaje: {}" + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error procesando el mensaje: {}" + e.getMessage());
            e.printStackTrace();
      }
    }

}
