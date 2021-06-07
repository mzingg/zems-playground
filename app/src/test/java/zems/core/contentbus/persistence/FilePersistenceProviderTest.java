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
import static zems.core.TestUtils.*;

class FilePersistenceProviderTest {

    private final ZemsJsonUtils jsonUtils = new ZemsJsonUtils();

    @Test
    void ctorWithNullContentPathThrowsExcpetion() {
        assertThrows(NullPointerException.class, () -> new FilePersistenceProvider(null, Path.of(".")));
    }

    @Test
    void ctorWithNullBinaryPathThrowsExcpetion() {
        assertThrows(NullPointerException.class, () -> new FilePersistenceProvider(Path.of("."), null));
    }

    @Test
    void writeWithContentDirNotExistingThrowsException() {
        assertThrows(IllegalStateException.class, () -> new FilePersistenceProvider(Path.of("nonexisting"), aTestPath())
          .write(new Content("/path", new InMemoryProperties()))
        );
    }

    @Test
    void writeWithValidContentWritesRelativeJsonFile() throws IOException {
        Path contentDirectory = aTestDirectory("content");
        Path binaryDirectory = aTestDirectory("binaries");
        FilePersistenceProvider testObj = new FilePersistenceProvider(contentDirectory, binaryDirectory);
        Content content = new Content(
          "/a/subpath",
          InMemoryProperties.of("hallo", "velo", "number", 42, "pi", 3.14159)
        );

        testObj.write(content);

        assertTrue(Files.isDirectory(contentDirectory.resolve("./a/subpath")));
        Path actualFilePath = contentDirectory.resolve("./a/subpath/.properties.json");
        String actualFileContent = Files.readString(actualFilePath);
        assertTrue(Files.exists(actualFilePath));
        assertEquals(jsonUtils.asJsonString(content.properties()), actualFileContent);
    }

    @Test
    void readWithValidContentReturnsContent() {
        Path contentDirectory = aTestDirectory("content");
        Path binaryDirectory = aTestDirectory("binaries");
        FilePersistenceProvider testObj = new FilePersistenceProvider(contentDirectory, binaryDirectory);
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
        Path contentDirectory = aTestDirectory("content");
        Path binaryDirectory = aTestDirectory("binaries");
        FilePersistenceProvider testObj = new FilePersistenceProvider(contentDirectory, binaryDirectory);

        Optional<Content> actual = testObj.read("/a/nonexistent/path");

        assertTrue(actual.isEmpty());
    }

    @Test
    void loadAndSaveFromJsonCreatesDirectoryWithContentsFromJson() {
        Path contentDirectory = aTestDirectory("content");
        Path binaryDirectory = aTestDirectory("binaries");

        new FilePersistenceProvider(contentDirectory, binaryDirectory)
          .initFromJson("zems/core/ContentBus/initialState.json");

        assertTrue(Files.exists(contentDirectory.resolve("./content/playground/de/de/.properties.json")));
        assertTrue(Files.exists(contentDirectory.resolve("./content/playground/de/de/contentParsys.json")));
        assertTrue(Files.exists(contentDirectory.resolve("./content/playground/de/de/contentParsys/components8.json")));
    }

    @AfterAll
    static void cleanupDirectories() {
        cleanupTestDirectories();
    }

}