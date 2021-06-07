package zems.core.properties.value;

import zems.core.concept.Value;

import java.nio.ByteBuffer;

public record FractionalValue(Double value) implements Value<Double> {
    public FractionalValue() {
        this(0D);
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putDouble(value());
    }

    @Override
    public Value<Double> unpack(ByteBuffer buffer) {
        return new FractionalValue(buffer.getDouble());
    }

    @Override
    public int packSize() {
        return Double.BYTES;
    }

}
