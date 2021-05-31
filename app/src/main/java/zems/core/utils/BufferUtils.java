package zems.core.utils;

import org.graalvm.collections.Pair;

import java.nio.ByteBuffer;

public class BufferUtils {

  public static void packString(String value, ByteBuffer buffer) {
    char[] valueCharArray = value.toCharArray();
    buffer.putInt(valueCharArray.length); // length of the String
    for (char c : valueCharArray) { // each character
      buffer.putChar(c);
    }
  }

  public static Pair<String, Integer> unpackString(ByteBuffer buffer) {
    int length = buffer.getInt();
    char[] valueCharArray = new char[length];
    for (int i = 0; i < length; i++) {
      valueCharArray[i] = buffer.getChar();
    }
    return Pair.create(new String(valueCharArray), Integer.BYTES + length * Character.BYTES);
  }

}
