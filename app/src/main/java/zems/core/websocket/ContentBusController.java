package zems.core.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import zems.core.concept.ContentBus;
import zems.core.contentbus.TransactionalContentBus;

@Controller
public class ContentBusController {

  private final ContentBus contentBus = new TransactionalContentBus();

  @MessageMapping("/contentbus/get")
  @SendTo("/topic/contentbus")
  public ContentBusGetResponse getModel(ContentBusGet message) {
    return new ContentBusGetResponse("get", message.clientId(), message.path(), contentBus.read(message.path()).get().properties());
  }

  @MessageMapping("/contentbus/update")
  @SendTo("/topic/contentbus")
  public ContentBusUpdateResponse updateModel(ContentBusUpdate message) {
    return new ContentBusUpdateResponse("update", message.changedPath(), message.payload());
  }

}
