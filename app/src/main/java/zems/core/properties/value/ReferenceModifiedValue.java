package zems.core.properties.value;

import zems.core.concept.ModifiedValue;

public record ReferenceModifiedValue(String original, String value) implements ModifiedValue<String, String> {
}
