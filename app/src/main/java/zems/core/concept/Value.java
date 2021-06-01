package zems.core.concept;

import java.nio.ByteBuffer;

public interface Value<T> extends Packable<Value<T>> {

  T value();

  @Override
  default void pack(ByteBuffer buffer) {
    throw new UnsupportedOperationException();
  }

  @Override
  default Value<T> unpack(ByteBuffer buffer) {
    throw new UnsupportedOperationException();
  }

  @Override
  default int packSize() {
    throw new UnsupportedOperationException();
  }

}
