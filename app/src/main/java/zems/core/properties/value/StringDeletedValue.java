package zems.core.properties.value;

import zems.core.concept.DeletedValue;

public record StringDeletedValue(String original) implements DeletedValue<String> {
}
