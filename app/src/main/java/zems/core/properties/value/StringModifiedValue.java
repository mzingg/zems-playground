package zems.core.properties.value;

import zems.core.concept.ModifiedValue;

public record StringModifiedValue(String original, String value) implements ModifiedValue<String, String> {
}
