package zems.core.contentbus.value;

import zems.core.contentbus.Packable;

import java.nio.ByteBuffer;

public interface Value<BACKING_TYPE> extends Packable<Value<BACKING_TYPE>> {

  BACKING_TYPE value();

  default void pack(ByteBuffer buffer) {

  }

  default Value<BACKING_TYPE> unpack(ByteBuffer buffer) {
    throw new UnsupportedOperationException();
  }

  default int packSize() {
    return 0;
  }

}
