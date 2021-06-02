package zems.core.properties;

import zems.core.concept.Properties;
import zems.core.concept.Value;
import zems.core.properties.value.AnyValue;

import java.util.*;

public class InMemoryProperties extends BaseProperties {

  private final LinkedHashMap<String, Value<?>> store = new LinkedHashMap<>();

  public static Properties of(Object... keyValueList) {
    InMemoryProperties result = new InMemoryProperties();
    if (keyValueList != null) {
      if (keyValueList.length % 2 != 0) {
        throw new IllegalArgumentException("must provide a list with an even number of entries (key, value)");
      }
      for (int i = 0; i < keyValueList.length; i += 2) {
        result.put(
            keyValueList[i].toString(),
            keyValueList[i + 1]
        );
      }
    }
    return result;
  }

  public static Properties from(Map<String, Object> stringObjectMap) {
    InMemoryProperties result = new InMemoryProperties();
    if (stringObjectMap != null) {
      for (Map.Entry<String, Object> propEntry : stringObjectMap.entrySet()) {
        result.put(propEntry.getKey(), AnyValue.of(propEntry.getValue()));
      }
    }
    return result;
  }

  @Override
  public Optional<Value<?>> find(String key) {
    Objects.requireNonNull(key);

    if (store.containsKey(key)) {
      return Optional.of(store.get(key));
    }
    return Optional.empty();
  }

  @Override
  public Properties put(String key, Value<?> value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);

    store.put(key, value);

    return this;
  }

  @Override
  public boolean isEmpty() {
    return store.isEmpty();
  }

  @Override
  public Set<String> keys() {
    return store.keySet();
  }

  @Override
  public Properties reset() {
    store.clear();

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InMemoryProperties that = (InMemoryProperties) o;
    return store.equals(that.store);
  }

  @Override
  public int hashCode() {
    return Objects.hash(store);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", InMemoryProperties.class.getSimpleName() + "[", "]")
        .add("store=" + store)
        .toString();
  }
}
