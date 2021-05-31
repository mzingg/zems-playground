package zems.core.contentbus.value;

public record ReferenceModifiedValue(String original, String value) implements ModifiedValue<String, String> {
}
