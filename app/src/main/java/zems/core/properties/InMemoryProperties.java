package zems.core.properties;

import zems.core.concept.Properties;
import zems.core.concept.Value;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class InMemoryProperties extends BaseProperties {

  private final LinkedHashMap<String, Value<?>> store = new LinkedHashMap<>();

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

}
