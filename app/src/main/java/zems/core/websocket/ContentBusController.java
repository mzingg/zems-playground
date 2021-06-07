package zems.core.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import zems.core.concept.Content;
import zems.core.concept.ContentBus;
import zems.core.properties.InMemoryProperties;

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
        String changedPath = message.changedPath();
        Content modifiedContent = contentBus
          .read(changedPath)
          .map(c -> new Content(c.path(), InMemoryProperties.mutationFrom(c.properties(), message.payload())))
          .orElse(new Content(changedPath, InMemoryProperties.from(message.payload())));

        contentBus.write(modifiedContent);
        return new ContentBusUpdateResponse("update", changedPath, modifiedContent.properties());
    }

}
