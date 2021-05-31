package zems.core.contentbus;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesTest {

  @Test
  void getWithMatchingTypeRetunsEntry() {

    Properties testObj = new Properties()
        .put("test", "value")
        .put("hallo", 1234)
        .put("velo", 1.456);

//    assertThat(testObj.get("test", String.class).get().value()).isEqualTo("value");
  }

  @Test
  void packAndUnpackWorksAsExpected() {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Properties testObj = new Properties()
        .put("test", "value")
        .put("test2", "value2")
        .put("hallo", 1234)
        .put("velo", 1.456);
    Properties testObj2 = new Properties()
        .put("this", "should")
        .put("notbe", "intheactual");

    testObj.pack(buffer);
    testObj2.pack(buffer);
    buffer.flip().rewind();
    Properties actual = new Properties().unpack(buffer);

    assertThat(actual).isEqualTo(testObj);
  }
}