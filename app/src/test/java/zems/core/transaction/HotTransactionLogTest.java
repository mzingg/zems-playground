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
  void openWithoutSequencegeneratorThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog()
      ) {
        log.open();
      }
    });
  }

  @Test
  void appendWithoutEnableThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog().setSequenceGenerator(new SequenceGenerator())
      ) {
        log.append(new Content("/a/path", new Properties()));
      }
    });
  }

  @Test
  void openWithoutLogPathSetThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog().setSequenceGenerator(new SequenceGenerator())
              .setGreenHeadPath(aTestPath("hot.tn.head"))
      ) {
        log.open();
      }
    });
  }

  @Test
  void openWithoutHeadPathSetThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog().setSequenceGenerator(new SequenceGenerator())
              .setLogPath(aTestPath("hot.tn"))
      ) {
        log.open();
      }
    });
  }

  @Test
  void appendWithEnoughSpaceInHeadIsSuccessful() throws Exception {
    Path logPath = aTestPath("hot.tn");
    Path greenHeadPath = aTestPath("hot.green.tn");
    Path redHeadPath = aTestPath("hot.red.tn");

    try (
        HotTransactionLog log = new HotTransactionLog().setHeadBufferSize(256).setSequenceGenerator(new SequenceGenerator())
            .setLogPath(logPath)
            .setGreenHeadPath(greenHeadPath)
            .setRedHeadPath(redHeadPath)
    ) {
      log.open();
      log.append(new Content("/a/path", new Properties()));
      log.append(new Content("/a/second/path", new Properties().put("hallo", "velo")));
      log.append(new Content("/a/third/path", new Properties().put("third", 1234234)));
    }

    System.out.println(">>> RED");
    try (
        TransactionLog log = new TransactionLog(new SequenceGenerator())
            .setLogPath(redHeadPath)
    ) {
      log.read().forEach(System.out::println);
    }

    System.out.println(">>> GREEN");
    try (
        TransactionLog log = new TransactionLog(new SequenceGenerator())
            .setLogPath(greenHeadPath)
    ) {
      log.read().forEach(System.out::println);
    }

    System.out.println(">>> LOG");
    try (
        TransactionLog log = new TransactionLog(new SequenceGenerator())
            .setLogPath(logPath)
    ) {
      log.read().forEach(System.out::println);
    }
  }

  @Test
  void appendWhileReachingEndOfHeadSwitchesHeadBuffer() throws Exception {
    Path logPath = aTestPath("hot.tn");
    Path greenHeadPath = aTestPath("hot.green.tn");
    Path redHeadPath = aTestPath("hot.red.tn");

    try (
        HotTransactionLog log = new HotTransactionLog().setSequenceGenerator(new SequenceGenerator())
            .setLogPath(logPath)
            .setGreenHeadPath(greenHeadPath)
            .setRedHeadPath(redHeadPath)
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
