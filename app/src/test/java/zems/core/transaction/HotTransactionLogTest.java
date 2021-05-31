package zems.core.transaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import zems.core.contentbus.Content;
import zems.core.contentbus.Properties;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static zems.core.TestUtils.aTestPath;
import static zems.core.TestUtils.cleanupTestDirectories;


public class HotTransactionLogTest {

  @Test
  void appendWithoutEnableThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog(new SequenceGenerator())
      ) {
        log.append(new Content("/a/path", new Properties()));
      }
    });
  }

  @Test
  void enableWithoutLogPathSetThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog(new SequenceGenerator())
              .setHeadPath(aTestPath("hot.tn.head"))
      ) {
        log.open();
      }
    });
  }

  @Test
  void enableWithoutHeadPathSetThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog(new SequenceGenerator())
              .setLogPath(aTestPath("hot.tn"))
      ) {
        log.open();
      }
    });
  }

  @Test
  void writeWithEnoughSpaceInHeadIsSuccessful() throws Exception {
    Path logPath = aTestPath("hot.tn");
    Path headPath = aTestPath("hot.tn.head");

    try (
        HotTransactionLog log = new HotTransactionLog(new SequenceGenerator())
            .setLogPath(logPath)
            .setHeadPath(headPath)
    ) {
      log.open();
      log.append(new Content("/a/path", new Properties()));
    }

  }

  @AfterAll
  static void afterAll() throws IOException {
    cleanupTestDirectories();
  }

}
