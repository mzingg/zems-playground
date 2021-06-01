package zems.core.properties.value;

import zems.core.concept.Value;

import java.nio.ByteBuffer;

public record BooleanValue(Boolean value) implements Value<Boolean> {
  public BooleanValue() {
    this(false);
  }

  @Override
  public void pack(ByteBuffer buffer) {
    buffer.put((byte) (value ? 1 : 0));
  }

  @Override
  public Value<Boolean> unpack(ByteBuffer buffer) {
    return new BooleanValue(buffer.get() == 1);
  }

  @Override
  public int packSize() {
    return 1;
  }

}
