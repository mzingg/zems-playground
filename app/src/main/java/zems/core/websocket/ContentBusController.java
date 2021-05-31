package zems.core.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import zems.core.contentbus.ContentBusSimple;
import zems.core.contentbus.InMemoryContentBusSimple;

@Controller
public class ContentBusController {

  private final ContentBusSimple contentBusSimple = new InMemoryContentBusSimple()
      .withInitialState("zems/core/ContentBus/initialState.json");

  @MessageMapping("/contentbus/get")
  @SendTo("/topic/contentbus")
  public ContentBusGetResponse getModel(ContentBusGet message) {
    return new ContentBusGetResponse("get", message.clientId(), message.path(), contentBusSimple.getProperties(message.path()));
  }

  @MessageMapping("/contentbus/update")
  @SendTo("/topic/contentbus")
  public ContentBusUpdateResponse updateModel(ContentBusUpdate message) {
    return new ContentBusUpdateResponse("update", message.changedPath(), message.payload());
  }

}
