package zems.core.utils;

import org.graalvm.collections.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public class BufferUtils {

  public static byte RECORD_SEPARATOR_BYTE = 'O';

  public static void packString(String value, ByteBuffer buffer) {
    char[] valueCharArray = value.toCharArray();
    buffer.putInt(valueCharArray.length * Character.BYTES); // length of the String
    for (char c : valueCharArray) { // each character
      buffer.putChar(c);
    }
  }

  public static Pair<String, Integer> unpackString(ByteBuffer buffer) {
    int byteLength = buffer.getInt();
    int stringLength = byteLength / Character.BYTES;
    char[] valueCharArray = new char[stringLength];
    for (int i = 0; i < stringLength; i++) {
      valueCharArray[i] = buffer.getChar();
    }
    return Pair.create(new String(valueCharArray), Integer.BYTES + byteLength);
  }

  /**
   * Thanks to https://stackoverflow.com/questions/25238110/how-to-properly-close-mappedbytebuffer
   * @param buffer
   */
  public static void disposeBuffer(MappedByteBuffer buffer) {
    if (buffer != null) {
      try {
        buffer.force();
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        Method invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
        invokeCleaner.invoke(unsafe, buffer);
      } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    }
  }
}
