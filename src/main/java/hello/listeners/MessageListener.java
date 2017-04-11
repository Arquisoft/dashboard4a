package hello.listeners;

import hello.MainController;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;

/**
 * Created by herminio on 28/12/16.
 */
@ManagedBean
public class MessageListener {

	private static final Logger logger = Logger.getLogger(MessageListener.class);
	// se guardan los mensajes recibidos en una lista
	public static List<String> mensajes = new ArrayList<String>();
	public static State state = new State("0");
	/** Counter for state changes. */
	private int counter = 1;

	@KafkaListener(topics = "exampleTopic")
	public void listen(String data) {
		synchronized (MainController.sseEmitters) {
			state = new State(String.valueOf(counter++));
			MainController.sseEmitters.forEach((SseEmitter emitter) -> {
				try {
					emitter.send(state, MediaType.APPLICATION_JSON);
					updatePositiveChart();
				} catch (IOException e) {
					emitter.complete();
					MainController.sseEmitters.remove(emitter);
				}
			});
		}
		// se añade el mensaje recibido a la lista
		mensajes.add(data);
		logger.info("New message received: \"" + data + "\"");
	}

	private void updatePositiveChart() {
		
	}

}
