package jeronimo.margitic.messaging_rabbitmq;

import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Component;

@Component
public class Receiver {
    
    // Ver : https://spring.io/guides/gs/messaging-rabbitmq

    // This lets it signal that the message has been received. 
    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage (String message) {
        System.out.println("Recibido <"+message+">");
        latch.countDown();
    }

    public CountDownLatch getLatch(){
        return latch;
    }

}
