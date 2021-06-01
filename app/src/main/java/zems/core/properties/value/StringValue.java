package zems.core.properties.value;

import zems.core.concept.Value;

import java.nio.ByteBuffer;

import static zems.core.utils.ZemsIoUtils.packString;
import static zems.core.utils.ZemsIoUtils.unpackString;

public record StringValue(String value) implements Value<String> {

  public StringValue() {
    this("");
  }

  @Override
  public void pack(ByteBuffer buffer) {
    packString(value(), buffer);
  }

  @Override
  public Value<String> unpack(ByteBuffer buffer) {
    return new StringValue(unpackString(buffer).getLeft());
  }

  @Override
  public int packSize() {
    return Integer.BYTES + (value().length() * Character.BYTES);
  }
}
