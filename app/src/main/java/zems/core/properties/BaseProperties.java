package zems.core.properties;

import org.graalvm.collections.Pair;
import zems.core.concept.*;
import zems.core.properties.value.AnyValue;
import zems.core.properties.value.BinaryValue;
import zems.core.properties.value.ReferenceValue;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import static zems.core.utils.ZemsIoUtils.packString;
import static zems.core.utils.ZemsIoUtils.unpackString;

public abstract class BaseProperties implements Properties {

  @Override
  public Optional<ContentReference> findReference(String key) {
    return find(key)
        .filter(candidate -> candidate instanceof ReferenceValue)
        .map(value -> (ContentReference) value);
  }

  @Override
  public Optional<BinaryReference> findBinary(String key) {
    return find(key)
        .filter(candidate -> candidate instanceof BinaryValue)
        .map(value -> (BinaryReference) value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> get(String key, Class<T> className) {
    return (Optional<T>) find(key)
        .filter(candidate -> candidate.value().getClass().isAssignableFrom(className))
        .map(Value::value);
  }

  public Properties put(String key, String value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    return put(key, AnyValue.of(value));
  }

  @Override
  public Properties put(String key, byte value) {
    Objects.requireNonNull(key);
    return put(key, AnyValue.of(value));
  }

  @Override
  public Properties put(String key, short value) {
    Objects.requireNonNull(key);
    return put(key, AnyValue.of(value));
  }

  public Properties put(String key, int value) {
    Objects.requireNonNull(key);
    return put(key, AnyValue.of(value));
  }

  public Properties put(String key, long value) {
    Objects.requireNonNull(key);
    return put(key, AnyValue.of(value));
  }

  public Properties put(String key, float value) {
    Objects.requireNonNull(key);
    return put(key, AnyValue.of(value));
  }

  public Properties put(String key, double value) {
    Objects.requireNonNull(key);
    return put(key, AnyValue.of(value));
  }

  public Properties put(String key, boolean value) {
    Objects.requireNonNull(key);
    return put(key, AnyValue.of(value));
  }

  public Properties unpack(ByteBuffer buffer) {
    try {
      reset();

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
    for (String key : keys()) {
      Value<?> value = find(key).orElseThrow();
      packString(key, buffer);
      packString(value.getClass().getName(), buffer);
      value.pack(buffer);
    }
  }

  public int packSize() {
    int dataSizeSum = 0;
    if (!isEmpty()) {
      dataSizeSum += Integer.BYTES;
      for (String key : keys()) {
        Value<?> value = find(key).orElseThrow();
        dataSizeSum += Integer.BYTES; // key string length
        dataSizeSum += key.length() * Character.BYTES;

        dataSizeSum += Integer.BYTES; // className string length
        dataSizeSum += value.getClass().getName().length() * Character.BYTES;

        dataSizeSum += value.packSize();
      }
    }
    return dataSizeSum;
  }

}
