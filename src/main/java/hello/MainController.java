package hello;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class MainController {

    private static final Logger logger = Logger.getLogger(MainController.class);
    private List<SseEmitter> sseEmitters = Collections.synchronizedList(new ArrayList<>());
    

    @RequestMapping("/")
    public String landing(Model model) {
        return "index";
    }


    @RequestMapping("/updates")
	SseEmitter subscribeUpdates() {
		SseEmitter sseEmitter = new SseEmitter();
		synchronized (this.sseEmitters) {
			this.sseEmitters.add(sseEmitter);
			sseEmitter.onCompletion(() -> {
				synchronized (this.sseEmitters) {
					this.sseEmitters.remove(sseEmitter);
				}
			});
		}
		return sseEmitter;
	}
    
    @RequestMapping(path = "/", method = RequestMethod.POST)
	@KafkaListener(topics="exampleTopic")
	public String showMessage(String data) {
		
			for (SseEmitter sseEmitter : this.sseEmitters) {
				try {
					sseEmitter.send(data);
				} catch (Exception e) {
					logger.error("Se ha cerrado el navegador");
				}
			}
		
		return data;
	}
    /**
     * Se ejecuta en localhost:8090/ejemplo.
     * Esto actualizara la pagina index con los mensajes de la lista
     * @param model
     * @return
     */
    @RequestMapping("/ejemplo")
	public String ejemplotest(Model model) {
		model.addAttribute("data", hello.listeners.MessageListener.mensajes);
		return "index";
	}

}