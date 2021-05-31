package zems.core.contentbus.value;

import java.nio.channels.ByteChannel;

public record BinaryModifiedValue(VoidType original, ByteChannel value) implements ModifiedValue<VoidType, ByteChannel> {
}
