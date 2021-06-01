package zems.core.properties.value;

import zems.core.concept.DeletedValue;
import zems.core.concept.VoidType;

public record BinaryDeletedValue(VoidType original) implements DeletedValue<VoidType> {
}
