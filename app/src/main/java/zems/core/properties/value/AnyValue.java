package zems.core.properties.value;

import zems.core.concept.Value;

import java.util.Objects;

public interface AnyValue<I> extends Value<I> {

  static <T> Value<T> of(Value<T> value) {
    Objects.requireNonNull(value);

    return of(value.value());
  }

  @SuppressWarnings("unchecked")
  static <T> Value<T> of(T value) {
    Objects.requireNonNull(value);

    if (value instanceof String) {
      return (Value<T>) new StringValue((String) value);
    } else if (value instanceof Long) {
      return (Value<T>) new NumberValue((Long) value);
    } else if (value instanceof Integer) {
      return (Value<T>) new NumberValue(((Integer) value).longValue());
    } else if (value instanceof Short) {
      return (Value<T>) new NumberValue(((Short) value).longValue());
    } else if (value instanceof Byte) {
      return (Value<T>) new NumberValue(((Byte) value).longValue());
    } else if (value instanceof Double) {
      return (Value<T>) new FractionalValue((Double) value);
    } else if (value instanceof Float) {
      return (Value<T>) new FractionalValue(((Float) value).doubleValue());
    } else if (value instanceof Boolean) {
      return (Value<T>) new BooleanValue((Boolean) value);
    }

    throw new IllegalArgumentException("Unsupported value " + value);
  }

}
