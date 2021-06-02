package zems.core.contentbus.persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import zems.core.concept.Content;
import zems.core.properties.InMemoryProperties;
import zems.core.utils.ZemsJsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static zems.core.TestUtils.aTestPath;
import static zems.core.TestUtils.cleanupTestDirectories;

class FilePersistenceProviderTest {

  private final ZemsJsonUtils jsonUtils = new ZemsJsonUtils().withOwnObjectMapper();

  @Test
  void ctorWithContainerDirectoryNullThrowsExcpetion() {
    assertThrows(NullPointerException.class, () -> new FilePersistenceProvider(null));
  }

  @Test
  void writeWithContainerDirNotExistingThrowsException() {
    assertThrows(IllegalStateException.class, () -> new FilePersistenceProvider(
        Path.of("nonexisting")).write(new Content("/path", new InMemoryProperties()))
    );
  }

  @Test
  void writeWithValidContentWritesRelativeJsonFile() throws IOException {
    Path containerDirectory = aTestPath();
    FilePersistenceProvider testObj = new FilePersistenceProvider(containerDirectory);
    Content content = new Content(
        "/a/subpath",
        InMemoryProperties.of("hallo", "velo", "number", 42, "pi", 3.14159)
    );

    testObj.write(content);

    assertTrue(Files.isDirectory(containerDirectory.resolve("./a/subpath")));
    Path actualFilePath = containerDirectory.resolve("./a/subpath/.properties.json");
    String actualFileContent = Files.readString(actualFilePath);
    assertTrue(Files.exists(actualFilePath));
    assertEquals(jsonUtils.asJsonString(content.properties()), actualFileContent);
  }

  @Test
  void readWithValidContentReturnsContent() {
    Path containerDirectory = aTestPath();
    FilePersistenceProvider testObj = new FilePersistenceProvider(containerDirectory);
    final String contentPath = "/a/subpath";
    Content expected = new Content(
        contentPath,
        InMemoryProperties.of("hallo", "velo", "number", 42, "pi", 3.14159)
    );
    testObj.write(expected);

    Content actual = testObj.read(contentPath).orElseThrow();

    assertEquals(expected, actual);
  }

  @Test
  void readWithoutExistingContentReturnsEmpty() {
    Path containerDirectory = aTestPath();
    FilePersistenceProvider testObj = new FilePersistenceProvider(containerDirectory);

    Optional<Content> actual = testObj.read("/a/nonexistent/path");

    assertTrue(actual.isEmpty());
  }

  @AfterAll
  static void cleanupDirectories() throws IOException {
    cleanupTestDirectories();
  }


}