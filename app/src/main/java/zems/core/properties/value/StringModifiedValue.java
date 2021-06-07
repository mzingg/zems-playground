package zems.core.properties.value;

import zems.core.concept.ModifiedValue;
import zems.core.concept.Value;

import java.nio.ByteBuffer;

import static zems.core.utils.ZemsIoUtils.packString;
import static zems.core.utils.ZemsIoUtils.unpackString;

public record StringModifiedValue(String original, String value) implements ModifiedValue<String, String> {

    public StringModifiedValue() {
        this("", "");
    }

    @Override
    public void pack(ByteBuffer buffer) {
        packString(original(), buffer);
        packString(value(), buffer);
    }

    @Override
    public Value<String> unpack(ByteBuffer buffer) {
        return new StringModifiedValue(
          unpackString(buffer).getLeft(),
          unpackString(buffer).getLeft()
        );
    }

    @Override
    public int packSize() {
        return 2 * Integer.BYTES + (original().length() * Character.BYTES) + (value().length() * Character.BYTES);
    }

}
