package zems.core.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import zems.core.concept.ContentBus;

@Controller
public class ContentBusController {

  private final ContentBus contentBus;

  public ContentBusController(ContentBus contentBus) {
    this.contentBus = contentBus;
  }

  @MessageMapping("/contentbus/get")
  @SendTo("/topic/contentbus")
  public ContentBusGetResponse getModel(ContentBusGet message) {
    return new ContentBusGetResponse("get", message.clientId(), message.path(), contentBus.read(message.path()).orElseThrow().properties());
  }

  @MessageMapping("/contentbus/update")
  @SendTo("/topic/contentbus")
  public ContentBusUpdateResponse updateModel(ContentBusUpdate message) {
    return new ContentBusUpdateResponse("update", message.changedPath(), message.payload());
  }

}
