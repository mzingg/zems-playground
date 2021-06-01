package zems.core.properties.value;

import zems.core.concept.ModifiedValue;

public record FractionalModifiedValue(Double original, Double value) implements ModifiedValue<Double, Double> {
}
