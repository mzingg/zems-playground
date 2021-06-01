package zems.core.transaction;

import org.junit.jupiter.api.Test;
import zems.core.concept.Properties;
import zems.core.properties.InMemoryProperties;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionSegmentTest {

  @Test
  void bufferOperationTest() {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Properties data = new InMemoryProperties()
        .put("aNumber", 1234567)
        .put("hallo", "velo");

    // first write object into the buffer
    TransactionSegment obj = new TransactionSegment()
        .setPath("/a/path")
        .setSequenceId(1234)
        .setData(data);
    obj.pack(buffer);

    // set the buffer into read mode
    buffer.flip();
    buffer.rewind();

    // and then read it out to verify
    TransactionSegment actual = new TransactionSegment().unpack(buffer);

    assertEquals("/a/path", actual.getPath());
    assertEquals(1234, actual.getSequenceId());
    assertEquals(data, actual.getData());
  }
}