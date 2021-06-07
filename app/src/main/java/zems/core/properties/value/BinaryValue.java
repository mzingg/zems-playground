package zems.core.properties.value;

import zems.core.concept.BinaryReference;
import zems.core.concept.ContentBus;
import zems.core.concept.Value;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import static zems.core.utils.ZemsIoUtils.packString;
import static zems.core.utils.ZemsIoUtils.unpackString;

public record BinaryValue(String value, ByteChannel data) implements BinaryReference {
    public BinaryValue() {
        this("", null);
    }

    @Override
    public BinaryValue resolve(ContentBus contentBus) {
        if (isNotResolved()) {
            return new BinaryValue(
              value(),
              contentBus.readBinary(value())
                .orElseThrow(IllegalArgumentException::new)
            );
        }
        return this;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        packString(value(), buffer);
    }

    @Override
    public Value<String> unpack(ByteBuffer buffer) {
        return new BinaryValue(unpackString(buffer).getLeft(), null);
    }

    @Override
    public int packSize() {
        return Integer.BYTES + (value().length() * Character.BYTES);
    }

}
