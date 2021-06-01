package zems.core.properties.value;

import zems.core.concept.ModifiedValue;
import zems.core.concept.VoidType;

import java.nio.channels.ByteChannel;

public record BinaryModifiedValue(VoidType original, ByteChannel value) implements ModifiedValue<VoidType, ByteChannel> {
}
