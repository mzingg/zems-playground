package zems.playground.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ContentBusController {

  private static final Logger LOG = LoggerFactory.getLogger(ContentBusController.class);

  private static final Map<String, Object> PAGE_PROPERTIES = Map.of(
      "pageTitle", "A Page Title from the server",
      "contentParsys", Map.of("components", List.of(
          Map.of(
              "resourceType", "zems/playground/TextImage",
              "path", "/content/playground/de/de>contentParsys:one"
          ),
          Map.of(
              "resourceType", "zems/playground/Text",
              "path", "/content/playground/de/de>contentParsys:two"
          )
      ))
  );

  @Autowired
  private SimpMessagingTemplate webSocket;

  @MessageMapping("/contentbus/get")
  @SendTo("/topic/contentbus")
  public GetModelResponse getModel(GetModelMessage message) {
    LOG.info(String.format("Handling message for path %s", message.getPath()));

    Object props = Map.of();
    if (message.getPath().equals("/content/playground/de/de")) {
      props = PAGE_PROPERTIES;
    } else if (message.getPath().equals("/content/playground/de/de>contentParsys")) {
      props = PAGE_PROPERTIES.get("contentParsys");
    }

    webSocket.convertAndSend("/topic/contentbus", "{\"text\": \"an additional message\"}");

    return new GetModelResponse()
        .setPath(message.getPath())
        .setClientId(message.getClientId())
        .setProperties(props);
  }

}
