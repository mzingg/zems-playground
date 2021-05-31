package zems.core.contentbus;

import org.graalvm.collections.Pair;
import zems.core.contentbus.value.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static zems.core.utils.BufferUtils.packString;
import static zems.core.utils.BufferUtils.unpackString;

public class Properties implements Packable<Properties> {

  private final LinkedHashMap<String, Value<?>> store = new LinkedHashMap<>();

  public Properties put(String name, Value<?> value) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(value);

    store.put(name, value);

    return this;
  }

  public Properties put(String name, String value) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(value);

    return put(name, new StringValue(value));
  }


  public Properties put(String name, int value) {
    Objects.requireNonNull(name);

    return put(name, new NumberValue((long) value));

  }

  public Properties put(String name, long value) {
    Objects.requireNonNull(name);

    return put(name, new NumberValue(value));
  }

  public Properties put(String name, float value) {
    Objects.requireNonNull(name);

    return put(name, new FractionalValue((double) value));
  }

  public Properties put(String name, double value) {
    Objects.requireNonNull(name);

    return put(name, new FractionalValue(value));
  }

  public Properties put(String name, boolean value) {
    Objects.requireNonNull(name);

    return put(name, new BooleanValue(value));
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<Value<T>> get(String name, T className) {
    return Optional.of((Value<T>) store.get(name));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Properties that = (Properties) o;
    return store.equals(that.store);
  }

  @Override
  public int hashCode() {
    return Objects.hash(store);
  }

  public Properties unpack(ByteBuffer buffer) {
    try {
      store.clear();

      int dataLength = buffer.getInt();
      int remainingLength = dataLength - Integer.BYTES;
      while (buffer.hasRemaining() && remainingLength > 0) {
        Pair<String, Integer> key = unpackString(buffer);
        remainingLength -= key.getRight();
        Pair<String, Integer> className = unpackString(buffer);
        remainingLength -= className.getRight();

        Value<?> value = (Value<?>) Class.forName(className.getLeft()).getConstructor().newInstance();
        Value<?> unpackedValue = value.unpack(buffer);
        remainingLength -= unpackedValue.packSize();

        put(key.getLeft(), unpackedValue);
      }

    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }

    return this;
  }

  public void pack(ByteBuffer buffer) {
    buffer.putInt(packSize());
    for (Map.Entry<String, Value<?>> entry : store.entrySet()) {
      packString(entry.getKey(), buffer);
      packString(entry.getValue().getClass().getName(), buffer);
      entry.getValue().pack(buffer);
    }
  }

  public int packSize() {
    int dataSizeSum = 0;
    if (!store.isEmpty()) {
      dataSizeSum += Integer.BYTES;
      for (Map.Entry<String, Value<?>> entry : store.entrySet()) {
        dataSizeSum += Integer.BYTES; // key string length
        dataSizeSum += entry.getKey().length() * Character.BYTES;

        dataSizeSum += Integer.BYTES; // className string length
        dataSizeSum += entry.getValue().getClass().getName().length() * Character.BYTES;

        dataSizeSum += entry.getValue().packSize();
      }
    }
    return dataSizeSum;
  }
}
