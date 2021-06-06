package zems.core.properties.value;

import zems.core.concept.Value;

import java.util.*;

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
    } else if (value instanceof Iterable) {
      List<Value<?>> list = new ArrayList<>();
      ((Iterable<?>)value).forEach(c -> list.add(AnyValue.of(c)));
      return (Value<T>) new ListValue(Collections.unmodifiableList(list));
    } else if (value instanceof Map) {
      Map<String, Object> valueMap = (Map<String, Object>) value;
      if (valueMap.containsKey("loadFrom")) {
        return (Value<T>) new ReferenceValue(valueMap.get("loadFrom").toString());
      }
    }

    throw new IllegalArgumentException("Unsupported value " + value);
  }

  @SuppressWarnings("unchecked")
  static <T> Value<T> modifiedTo(T original, T value) {
    Objects.requireNonNull(original);

    if (value == null) {
      return deleted(original);
    }

    if (value instanceof String) {
      return (Value<T>) new StringModifiedValue((String) original, (String) value);
    } else if (value instanceof Long) {
      return (Value<T>) new NumberModifiedValue((Long) original, (Long) value);
    } else if (value instanceof Integer) {
      return (Value<T>) new NumberModifiedValue(((Integer)original).longValue(), ((Integer) value).longValue());
    } else if (value instanceof Short) {
      return (Value<T>) new NumberModifiedValue(((Short) original).longValue(), ((Short) value).longValue());
    } else if (value instanceof Byte) {
      return (Value<T>) new NumberModifiedValue(((Byte) original).longValue(), ((Byte) value).longValue());
    } else if (value instanceof Double) {
      return (Value<T>) new FractionalModifiedValue((Double) original, (Double) value);
    } else if (value instanceof Float) {
      return (Value<T>) new FractionalModifiedValue(((Float) original).doubleValue(), ((Float) value).doubleValue());
    } else if (value instanceof Boolean) {
      return (Value<T>) new BooleanModifiedValue((Boolean) original, (Boolean) value);
    }

    throw new IllegalArgumentException("Unsupported modified value " + value);
  }

  @SuppressWarnings("unchecked")
  static <T> Value<T> deleted(T original) {
    Objects.requireNonNull(original);

    if (original instanceof String) {
      return (Value<T>) new StringDeletedValue((String) original);
    } else if (original instanceof Long) {
      return (Value<T>) new NumberDeletedValue((Long) original);
    } else if (original instanceof Integer) {
      return (Value<T>) new NumberDeletedValue(((Integer)original).longValue());
    } else if (original instanceof Short) {
      return (Value<T>) new NumberDeletedValue(((Short) original).longValue());
    } else if (original instanceof Byte) {
      return (Value<T>) new NumberDeletedValue(((Byte) original).longValue());
    } else if (original instanceof Double) {
      return (Value<T>) new FractionalDeletedValue((Double) original);
    } else if (original instanceof Float) {
      return (Value<T>) new FractionalDeletedValue(((Float) original).doubleValue());
    } else if (original instanceof Boolean) {
      return (Value<T>) new BooleanDeletedValue((Boolean) original);
    }

    throw new IllegalArgumentException("Unsupported deleted value " + original);
  }

}
