package zems.core.contentbus.value;

public record FractionalModifiedValue(Double original, Double value) implements ModifiedValue<Double, Double> {
}
