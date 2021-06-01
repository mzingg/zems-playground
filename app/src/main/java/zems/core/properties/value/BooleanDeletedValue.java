package zems.core.properties.value;

import zems.core.concept.DeletedValue;

public record BooleanDeletedValue(Boolean original) implements DeletedValue<Boolean> {
}
