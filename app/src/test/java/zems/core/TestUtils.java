package zems.core;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class TestUtils {

  public static void cleanupTestDirectories() throws IOException {
    Path resourcesPath = Path.of("build/test-execution-resources");
    System.gc(); // ensure all open files are released
    FileUtils.deleteDirectory(resourcesPath.toFile());
  }

  public static Path aTestPath() {
    return aTestPath(".").normalize();
  }

  public static Path aTestPath(String fileName) {
    String testName = findFirstZemsClassMethodName(Thread.currentThread().getStackTrace(), "aTestPath");
    try {
      Path resourcesPath = Path.of("build/test-execution-resources");
      if (!Files.exists(resourcesPath)) {
        Files.createDirectory(resourcesPath);
      }
      Path testDirPath = resourcesPath.resolve(testName);
      if (!Files.exists(testDirPath)) {
        Files.createDirectory(testDirPath);
      }

      return testDirPath.resolve(fileName).normalize();
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }
  }

  private static String findFirstZemsClassMethodName(StackTraceElement[] stackTrace, String myself) {
    return Arrays.stream(stackTrace)
        .filter(st -> st.getClassName().startsWith("zems") && !st.getMethodName().equals(myself) && !st.getMethodName().startsWith("lambda"))
        .map(StackTraceElement::getMethodName)
        .findAny()
        .orElse("test-method-not-found");
  }

}
