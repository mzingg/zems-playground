package zems.playground.handler;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import zems.playground.api.ResourceType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ZemsRequestHandler {

  public static ServerResponse handleHtml(ServerRequest serverRequest) throws IOException {
    ResourceType resourceType = new ResourceType("zems/playground/page");
    Resource resource = new ClassPathResource(resourceType.resourceUri(".html"));

    ByteArrayOutputStream content = new ByteArrayOutputStream();
    try (ReadableByteChannel contentChannel = resource.readableChannel()) {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      while (contentChannel.read(buffer) > 0) {
        content.write(buffer.array(), 0, buffer.position());
        buffer.clear();
      }
    }

    return ServerResponse.ok()
        .contentType(MediaType.TEXT_HTML)
        .body(content.toString(StandardCharsets.UTF_8));
  }

  public static Optional<Resource> resourceLookup(ServerRequest serverRequest) {
    return Optional.empty();
  }

}
