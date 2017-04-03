package hello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import hello.listeners.MessageListener;
import hello.listeners.MessageListenerNegative;
import hello.productorPrueba.KafkaProducer;

@Controller
public class MainController {

	private static final Logger logger = Logger.getLogger(MainController.class);
	public static List<SseEmitter> sseEmitters = Collections.synchronizedList(new ArrayList<>());
	public static List<SseEmitter> sseEmitters2 = Collections.synchronizedList(new ArrayList<>());
	@Autowired
	private KafkaProducer kafkaProducer = new KafkaProducer();

	@RequestMapping("/votacion")
	public String votaciones() {
		return "index3";
	}

	@RequestMapping("/a")
	public String landing(Model model) {
		return "index";
	}

	@RequestMapping("/")
	public String sseExamplePage(Map<String, Object> model) {
		model.put("state", MessageListener.state);
		model.put("state2", MessageListenerNegative.state2);
		return "index";
	}

	@RequestMapping(path = "/register", method = RequestMethod.GET)
	public SseEmitter register() throws IOException {
		logger.info("Registering a stream.");

		SseEmitter emitter = new SseEmitter();

		synchronized (sseEmitters) {
			sseEmitters.add(emitter);
		}
		emitter.onCompletion(() -> sseEmitters.remove(emitter));

		return emitter;
	}

	@RequestMapping(path = "/register2", method = RequestMethod.GET)
	public SseEmitter register2() throws IOException {
		logger.info("Registering a stream2.");

		SseEmitter emitter = new SseEmitter();

		synchronized (sseEmitters2) {
			sseEmitters2.add(emitter);
		}
		emitter.onCompletion(() -> sseEmitters2.remove(emitter));

		return emitter;
	}

	@RequestMapping("/updates")
	SseEmitter subscribeUpdates() {
		SseEmitter sseEmitter = new SseEmitter();
		synchronized (sseEmitters) {
			sseEmitters.add(sseEmitter);
			sseEmitter.onCompletion(() -> {
				synchronized (sseEmitters) {
					sseEmitters.remove(sseEmitter);
				}
			});
		}
		return sseEmitter;
	}

	// @RequestMapping(path = "/", method = RequestMethod.POST)
	//
	// public String showMessage(String data) {
	//
	// for (SseEmitter sseEmitter : sseEmitters) {
	// try {
	// sseEmitter.send(data);
	// } catch (Exception e) {
	// logger.error("Se ha cerrado el navegador");
	// }
	// }
	//
	// return data;
	// }
	/**
	 * Se ejecuta en localhost:8090/ejemplo. Esto actualizara la pagina index
	 * con los mensajes de la lista
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/ejemplo")
	public String ejemplotest(Model model) {
		model.addAttribute("data", hello.listeners.MessageListener.mensajes);
		return "index";
	}

	@RequestMapping("/votoPositivo")
	public String loadData(Model model) {
		kafkaProducer.send("exampleTopic", "test");
		return "index3";
	}

	@RequestMapping("/votoNegativo")
	public String loadDataNegative(Model model) {
		kafkaProducer.send("negativeVote", "negativetest");
		return "index3";
	}

}