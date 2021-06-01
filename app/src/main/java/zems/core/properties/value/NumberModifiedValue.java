package zems.core.properties.value;

import zems.core.concept.ModifiedValue;

public record NumberModifiedValue(Long original, Long value) implements ModifiedValue<Long, Long> {
}
