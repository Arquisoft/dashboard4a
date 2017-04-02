package hello.listeners;

import hello.MainController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;

/**
 * Created by herminio on 28/12/16.
 */
@ManagedBean
public class MessageListener {

    private static final Logger logger = Logger.getLogger(MessageListener.class);
    //se guardan los mensajes recibidos en una lista
    public static List<String> mensajes = new ArrayList<String>();
    @KafkaListener(topics = "exampleTopic")
    public void listen(String data) {
    	//se añade el mensaje recibido a la lista
    	mensajes.add(data);
        logger.info("New message received: \"" + data + "\"");
    }



}
