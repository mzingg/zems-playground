package zems.core.contentbus.value;

public record StringModifiedValue(String original, String value) implements ModifiedValue<String, String> {
}
