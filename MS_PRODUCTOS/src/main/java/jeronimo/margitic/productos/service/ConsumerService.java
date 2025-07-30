package jeronimo.margitic.productos.service;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.*;

import jeronimo.margitic.productos.dto.ProductoDTO;

import java.util.List;
import java.util.Map;

@Service
public class ConsumerService {
    
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final ObjectMapper mapper = new ObjectMapper();

    public ConsumerService (String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void consume(String queueName, boolean isList) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            System.out.println("Esperando mensajes de la cola: " + queueName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    if (isList) {
                        List<ProductoDTO> productos = mapper.readValue(json, new TypeReference<List<ProductoDTO>>() {});
                        System.out.println("Recibida lista de personas:");
                        for (ProductoDTO p : productos) {
                            System.out.println("Id producto: " + p.getId_producto() + ", cantidad " + p.getCantidad());
                        }
                    } else {
                        ProductoDTO p = mapper.readValue(json, ProductoDTO.class);
                        System.out.println("Id producto: " + p.getId_producto() + ", cantidad " + p.getCantidad());
                    }
                } catch (Exception e) {
                    System.err.println("Error al deserializar el mensaje: " + e.getMessage());
                }
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        } catch (Exception e) {
            System.err.println("Error en el consumidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
