package zems.core.properties.value;

import zems.core.concept.Value;

import java.util.List;

public record ListValue(List<Value<?>> value) implements Value<List<Value<?>>> {
}
