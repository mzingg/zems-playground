package zems.core.properties;

import org.junit.jupiter.api.Test;
import zems.core.concept.Properties;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryPropertiesTest {

    @Test
    void getWithMatchingTypeRetunsEntry() {
        Properties testObj = new InMemoryProperties()
          .put("test", "value")
          .put("hallo", 1234)
          .put("flag", true)
          .put("velo", 1.456);

        assertEquals("value", testObj.get("test", String.class).orElse(""));
        assertEquals(1234, testObj.get("hallo", Long.class).orElse(0L));
        assertEquals(true, testObj.get("flag", Boolean.class).orElse(false));
        assertEquals(1.456, testObj.get("velo", Double.class).orElse(1D));
        assertTrue(testObj.get("nonexisting", String.class).isEmpty());
        assertTrue(testObj.get("hallo", Integer.class).isEmpty());
        assertTrue(testObj.get("velo", Float.class).isEmpty());
    }

    @Test
    void getWithNonExistingReturnsEmpty() {
        Properties testObj = new InMemoryProperties()
          .put("test", "value")
          .put("hallo", 1234)
          .put("velo", 1.456);

        assertTrue(testObj.get("nonexisting", String.class).isEmpty());
    }

    @Test
    void getWithUnsupportedTypesReturnsEmpty() {
        Properties testObj = new InMemoryProperties()
          .put("hallo", 1234)
          .put("velo", 1.456);

        assertTrue(testObj.get("hallo", Integer.class).isEmpty());
        assertTrue(testObj.get("velo", Float.class).isEmpty());
    }


    @Test
    void packAndUnpackWorksAsExpected() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Properties testObj = new InMemoryProperties()
          .put("test", "value")
          .put("test2", "value2")
          .put("hallo", 1234)
          .put("velo", 1.456);
        Properties testObj2 = new InMemoryProperties()
          .put("this", "should")
          .put("notbe", "intheactual");

        testObj.pack(buffer);
        testObj2.pack(buffer);
        buffer.flip().rewind();
        Properties actual = new InMemoryProperties().unpack(buffer);

        assertEquals(testObj, actual);
    }
}