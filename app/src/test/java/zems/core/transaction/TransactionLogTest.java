package zems.core.transaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import zems.core.contentbus.Content;
import zems.core.contentbus.Properties;

import java.io.IOException;
import java.nio.file.Path;

import static zems.core.TestUtils.aTestPath;
import static zems.core.TestUtils.cleanupTestDirectories;

class TransactionLogTest {

  @Test
  void writeTest() throws Exception {
    Path logPath = aTestPath("hot.tn");

    try (
        TransactionLog log = new TransactionLog(new SequenceGenerator())
            .setLogPath(logPath)
    ) {
      log.append(
          new Content("/a/path", new Properties().put("test", "test")),
          new Content("/a/path>test", new Properties().put("sub", "properties"))
      );

      log.read().forEach(System.out::println);
    }

  }

  @AfterAll
  static void afterAll() throws IOException {
    cleanupTestDirectories();
  }
}