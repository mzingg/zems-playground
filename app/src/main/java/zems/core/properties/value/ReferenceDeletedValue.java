package zems.core.properties.value;

import zems.core.concept.DeletedValue;

public record ReferenceDeletedValue(String original) implements DeletedValue<String> {
}
