package zems.core.properties.value;

import zems.core.concept.ModifiedValue;

public record BooleanModifiedValue(Boolean original, Boolean value) implements ModifiedValue<Boolean, Boolean> {
}
