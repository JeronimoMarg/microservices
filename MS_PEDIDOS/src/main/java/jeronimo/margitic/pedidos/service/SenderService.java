package jeronimo.margitic.pedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Service
public class SenderService {
    
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final ObjectMapper mapper = new ObjectMapper();

    public SenderService(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void sendMessage(String queueName, String message) {
        sendRaw(queueName, message);
    }

    public void sendObject(String queueName, Object obj) {
        try {
            String json = mapper.writeValueAsString(obj);
            sendRaw(queueName, json);
        } catch (Exception e) {
            System.err.println("Error serializando objeto: " + e.getMessage());
        }
    }

    public void sendList(String queueName, List<?> list) {
        try {
            String json = mapper.writeValueAsString(list);
            sendRaw(queueName, json);
        } catch (Exception e) {
            System.err.println("Error serializando lista: " + e.getMessage());
        }
    }

    private void sendRaw(String queueName, String message) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
            System.out.println("Mensaje enviado a '" + queueName + "': " + message);

        } catch (Exception e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
        }
    }
}
