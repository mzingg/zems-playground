package zems.core.contentbus.value;

public record BooleanModifiedValue(Boolean original, Boolean value) implements ModifiedValue<Boolean, Boolean> {
}
