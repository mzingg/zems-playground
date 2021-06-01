package zems.core.concept;

import java.util.Optional;
import java.util.Set;

public interface Properties extends Packable<Properties> {

  Set<String> keys();

  Optional<Value<?>> find(String key);

  Optional<ContentReference> findReference(String key);

  Optional<BinaryReference> findBinary(String key);

  <T> Optional<T> get(String key, Class<T> className);

  Properties put(String key, Value<?> value);

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
