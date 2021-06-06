package zems.core.concept;

import zems.core.properties.value.BinaryValue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface Properties extends Packable<Properties> {

  Set<String> keys();

  Optional<Value<?>> find(String key);

  Optional<ContentReference> findReference(String key);

  default Stream<ContentReference> references() {
    return keys().stream()
        .map(this::findReference)
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  Optional<BinaryReference> findBinary(String key);

  default Stream<BinaryReference> binaries() {
    return keys().stream()
        .map(this::findBinary)
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  <T> Optional<T> get(String key, Class<T> className);

  Properties put(String key, Value<?> value);

  Properties put(String key, Object value);

  Properties put(String key, String value);

  Properties put(String key, byte value);

  Properties put(String key, short value);

  Properties put(String key, int value);

  Properties put(String key, long value);

  Properties put(String key, float value);

  Properties put(String key, double value);

  Properties put(String key, boolean value);

  boolean isEmpty();

  Properties reset();

}
