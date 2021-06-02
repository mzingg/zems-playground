package zems.core.contentbus.transaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import zems.core.concept.Content;
import zems.core.concept.SequenceGenerator;
import zems.core.properties.InMemoryProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static zems.core.TestUtils.aTestPath;
import static zems.core.TestUtils.cleanupTestDirectories;

class MainTransactionLogTest {

  @Test
  void setReadBufferSizeWithTooSmallArgumentThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new MainTransactionLog().setReadBufferSize(128));
  }

  @Test
  void readReturnsAllThatWasAppended() {
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new InMemoryProperties().put("test", "test")),
        new Content("/a/path>test", new InMemoryProperties().put("sub", "properties"))
    };

    try (
        MainTransactionLog log = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      log.append(contentArray);
      assertTrue(log.read().allMatch(Arrays.asList(contentArray)::contains));
    }
  }

  @Test
  void readReachingTheEndOfTheReadBufferReturnsAllThatWasAppended() {
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new InMemoryProperties().put("test", "test lorem ipsum lorem ipsum  lorem ipsum")),
        new Content("/a/path>test", new InMemoryProperties().put("sub", "properties"))
    };

    try (
        MainTransactionLog log = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setReadBufferSize(256)
            .setLogPath(logPath)
    ) {
      log.append(contentArray);
      assertTrue(log.read().allMatch(Arrays.asList(contentArray)::contains));
    }
  }

  @Test
  void readWithLogContainingDataAfterSegmentThrowsException() throws IOException {
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new InMemoryProperties().put("test", "test lorem ipsum lorem ipsum  lorem ipsum")),
        new Content("/a/path>test", new InMemoryProperties().put("sub", "properties"))
    };
    try (
        MainTransactionLog log = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      // add some data
      log.append(contentArray);
    }
    Files.writeString(logPath, "some junk", APPEND);

    assertThrows(IllegalStateException.class, () -> {
      try (
          MainTransactionLog log = new MainTransactionLog()
              .setSequenceGenerator(sequenceGenerator)
              .setLogPath(logPath)
      ) {
        log.read();
      }
    });
  }

  @Test
  void readWithLogContainingDataAfterSegmentButAllowingSuperfluousDataReturnsData() throws IOException {
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new InMemoryProperties().put("test", "test lorem ipsum lorem ipsum  lorem ipsum")),
        new Content("/a/path>test", new InMemoryProperties().put("sub", "properties"))
    };
    try (
        MainTransactionLog log = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      // add some data
      log.append(contentArray);
    }
    Files.writeString(logPath, "some junk", APPEND);

    try (
        MainTransactionLog log = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(logPath)
    ) {
      assertTrue(log.read().allMatch(Arrays.asList(contentArray)::contains));
    }
  }


  @AfterAll
  static void cleanupDirectories() throws IOException {
    cleanupTestDirectories();
  }
}