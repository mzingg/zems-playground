package zems.core.contentbus.value;

public record NumberModifiedValue(Long original, Long value) implements ModifiedValue<Long, Long> {
}
