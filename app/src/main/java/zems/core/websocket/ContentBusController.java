package zems.core.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import zems.core.contentbus.ContentBus;
import zems.core.contentbus.InMemoryContentBus;

@Controller
public class ContentBusController {

  private final ContentBus contentBus = new InMemoryContentBus()
      .withInitialState("zems/core/ContentBus/initialState.json");

  @MessageMapping("/contentbus/get")
  @SendTo("/topic/contentbus")
  public ContentBusGetResponse getModel(ContentBusGet message) {
    return new ContentBusGetResponse()
        .setPath(message.getPath())
        .setClientId(message.getClientId())
        .setProperties(contentBus.getProperties(message.getPath()));
  }
}
