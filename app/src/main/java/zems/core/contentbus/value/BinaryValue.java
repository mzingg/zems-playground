package zems.core.contentbus.value;

import java.nio.channels.ByteChannel;

public record BinaryValue(ByteChannel value) implements Value<ByteChannel> {
  public BinaryValue() {
    this(null);
  }
}
