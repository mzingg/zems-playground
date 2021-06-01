package zems.core.properties.value;

import zems.core.concept.Value;

import java.nio.ByteBuffer;

public record NumberValue(Long value) implements Value<Long> {

  public NumberValue() {
    this(0L);
  }

  @Override
  public void pack(ByteBuffer buffer) {
    buffer.putLong(value());
  }

  @Override
  public Value<Long> unpack(ByteBuffer buffer) {
    return new NumberValue(buffer.getLong());
  }

  @Override
  public int packSize() {
    return Long.BYTES;
  }

}
