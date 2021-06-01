package zems.core.properties.value;

import zems.core.concept.DeletedValue;

public record FractionalDeletedValue(Double original) implements DeletedValue<Double> {
}
