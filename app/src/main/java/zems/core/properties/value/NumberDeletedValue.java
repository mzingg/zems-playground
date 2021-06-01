package zems.core.properties.value;

import zems.core.concept.DeletedValue;

public record NumberDeletedValue(Long original) implements DeletedValue<Long> {
}
