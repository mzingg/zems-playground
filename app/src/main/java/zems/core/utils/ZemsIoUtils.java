package zems.core.utils;

import org.graalvm.collections.Pair;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

public class ZemsIoUtils {

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
   *
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

  public static void zeroFile(Path path) {
    try (FileChannel channel = FileChannel.open(path, WRITE)) {
      long size = Files.size(path);
      int bufferSize = 1024;

      final ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
      channel.position(0);

      final int blocks = (int) (size / bufferSize);
      final int blockRemainder = (int) (size % bufferSize);

      for (int i = 0; i < blocks; i++) {
        byteBuffer.position(0);
        channel.write(byteBuffer);
      }

      if (blockRemainder > 0) {
        byteBuffer.position(0);
        byteBuffer.limit(blockRemainder);
        channel.write(byteBuffer);
      }
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }
  }

  public static void appendFileFrom(Path source, Path to, long amountOfBytes) {
    try (
        FileChannel targetChannel = FileChannel.open(to, APPEND);
        FileChannel sourceChannel = FileChannel.open(source, READ)
    ) {
      sourceChannel.transferTo(0, amountOfBytes, targetChannel);
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }
  }

}