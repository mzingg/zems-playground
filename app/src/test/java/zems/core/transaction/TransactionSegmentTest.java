package zems.core.transaction;

import org.junit.jupiter.api.Test;
import zems.core.contentbus.Properties;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionSegmentTest {

  @Test
  void bufferOperationTest() {
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    // first write object into the buffer
    TransactionSegment obj = new TransactionSegment()
        .setPath("/a/path")
        .setSequenceId(1234)
        .setData(new Properties());
    obj.pack(buffer);

    // set the buffer into read mode
    buffer.flip();
    buffer.rewind();

    // and then read it out to verify
    TransactionSegment actual = new TransactionSegment().unpack(buffer);

    assertThat(actual.getPath()).isEqualTo("/a/path");
    assertThat(actual.getSequenceId()).isEqualTo(1234);
//    assertThat(actual.getData()).isEqualTo(new byte[] {1, 2, 3});
  }
}