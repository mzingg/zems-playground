package zems.core.contentbus;

import java.nio.ByteBuffer;

public interface Packable<T> {

  void pack(ByteBuffer buffer);

  T unpack(ByteBuffer buffer);

  int packSize();

}
