package zems.core.transaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import zems.core.contentbus.Content;
import zems.core.contentbus.Properties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static zems.core.TestUtils.aTestPath;
import static zems.core.TestUtils.cleanupTestDirectories;

class TransactionLogTest {

  @Test
  void setReadBufferSizeWithTooSmallArgumentThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new TransactionLog().setReadBufferSize(128));
  }

  @Test
  void readReturnsAllThatWasAppended() {
    SequenceGenerator sequenceGenerator = new SequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new Properties().put("test", "test")),
        new Content("/a/path>test", new Properties().put("sub", "properties"))
    };

    try (
        TransactionLog log = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      log.append(contentArray);
      assertTrue(log.read().allMatch(Arrays.asList(contentArray)::contains));
    }
  }

  @Test
  void readReachingTheEndOfTheReadBufferReturnsAllThatWasAppended() {
    SequenceGenerator sequenceGenerator = new SequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new Properties().put("test", "test lorem ipsum lorem ipsum  lorem ipsum")),
        new Content("/a/path>test", new Properties().put("sub", "properties"))
    };

    try (
        TransactionLog log = new TransactionLog()
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
    SequenceGenerator sequenceGenerator = new SequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new Properties().put("test", "test lorem ipsum lorem ipsum  lorem ipsum")),
        new Content("/a/path>test", new Properties().put("sub", "properties"))
    };
    try (
        TransactionLog log = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      // add some data
      log.append(contentArray);
    }
    Files.writeString(logPath, "some junk", APPEND);

    assertThrows(IllegalStateException.class, () -> {
      try (
          TransactionLog log = new TransactionLog()
              .setSequenceGenerator(sequenceGenerator)
              .setLogPath(logPath)
      ) {
        log.read();
      }
    });
  }

  @Test
  void readWithLogContainingDataAfterSegmentButAllowingSuperfluousDataReturnsData() throws IOException {
    SequenceGenerator sequenceGenerator = new SequenceGenerator();
    Path logPath = aTestPath("hot.tn");
    Content[] contentArray = {
        new Content("/a/path", new Properties().put("test", "test lorem ipsum lorem ipsum  lorem ipsum")),
        new Content("/a/path>test", new Properties().put("sub", "properties"))
    };
    try (
        TransactionLog log = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      // add some data
      log.append(contentArray);
    }
    Files.writeString(logPath, "some junk", APPEND);

    try (
        TransactionLog log = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(logPath)
    ) {
      assertTrue(log.read().allMatch(Arrays.asList(contentArray)::contains));
    }
  }


  @AfterAll
  static void afterAll() throws IOException {
    cleanupTestDirectories();
  }
}