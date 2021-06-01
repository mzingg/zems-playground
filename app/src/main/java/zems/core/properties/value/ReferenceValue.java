package zems.core.properties.value;

import zems.core.concept.Content;
import zems.core.concept.ContentBus;
import zems.core.concept.ContentReference;

import java.nio.ByteBuffer;

import static zems.core.utils.ZemsIoUtils.packString;
import static zems.core.utils.ZemsIoUtils.unpackString;

public record ReferenceValue(String value, Content data) implements ContentReference {

  public ReferenceValue() {
    this("", null);
  }

  @Override
  public ReferenceValue resolve(ContentBus contentBus) {
    if (isNotResolved()) {
      return new ReferenceValue(
          value(),
          contentBus.read(value())
              .orElseThrow(IllegalArgumentException::new)
      );
    }
    return this;
  }

  @Override
  public void pack(ByteBuffer buffer) {
    packString(value(), buffer);
  }

  @Override
  public ReferenceValue unpack(ByteBuffer buffer) {
    return new ReferenceValue(unpackString(buffer).getLeft(), null);
  }

  @Override
  public int packSize() {
    return Integer.BYTES + (value().length() * Character.BYTES);
  }

}
