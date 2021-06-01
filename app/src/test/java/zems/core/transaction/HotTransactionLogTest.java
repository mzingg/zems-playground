package zems.core.transaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import zems.core.contentbus.Content;
import zems.core.contentbus.Properties;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
          HotTransactionLog log = new HotTransactionLog()
              .setSequenceGenerator(new SequenceGenerator())
      ) {
        log.append(new Content("/a/path", new Properties()));
      }
    });
  }

  @Test
  void openWithoutLogPathSetThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog()
              .setSequenceGenerator(new SequenceGenerator())
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
          HotTransactionLog log = new HotTransactionLog()
              .setSequenceGenerator(new SequenceGenerator())
      ) {
        log.open();
      }
    });
  }

  @Test
  void setHeadBufferSizeWithTooSmallArgumentThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new HotTransactionLog().setHeadBufferSize(128));
  }

  @Test
  void appendWithEnoughSpaceInHeadIsSuccessful() throws Exception {
    Path logPath = aTestPath("hot.tn");
    Path greenHeadPath = aTestPath("hot.green.tn");
    Path redHeadPath = aTestPath("hot.red.tn");
    SequenceGenerator sequenceGenerator = new SequenceGenerator();
    List<Content> contentList = Arrays.asList(
        new Content("/a/path", new Properties()),
        new Content("/a/second/path", new Properties().put("hallo", "velo")),
        new Content("/a/third/path", new Properties().put("third", 1234234))
    );

    try (
        TransactionLog commitLog = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath);
        HotTransactionLog log = new HotTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setCommitLog(commitLog)
            .setGreenHeadPath(greenHeadPath)
            .setRedHeadPath(redHeadPath)
    ) {
      log.open();
      contentList.forEach(log::append);
    }

    try (
        TransactionLog redLogAfterOperation = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(redHeadPath);
        TransactionLog greenLogAfterOperation = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(greenHeadPath);
        TransactionLog commitLogAfterOperation = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      List<Content> redLog = redLogAfterOperation.read().toList();
      List<Content> greenLog = greenLogAfterOperation.read().toList();
      List<Content> commitLog = commitLogAfterOperation.read().toList();

      assertTrue(redLog.containsAll(contentList));
      assertTrue(greenLog.isEmpty());
      assertTrue(commitLog.isEmpty());
    }
  }

  @Test
  void appendWhileReachingEndOfHeadSwitchesHeadBuffer() throws Exception {
    Path logPath = aTestPath("hot.tn");
    Path greenHeadPath = aTestPath("hot.green.tn");
    Path redHeadPath = aTestPath("hot.red.tn");
    SequenceGenerator sequenceGenerator = new SequenceGenerator();
    List<Content> contentList = Arrays.asList(
        new Content("/a/path", new Properties()),
        new Content("/a/second/path", new Properties().put("hallo", "velo lorem ipsum")),
        new Content("/a/third/path", new Properties().put("third", 1234234))
    );

    try (
        TransactionLog commitLog = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath);
        HotTransactionLog log = new HotTransactionLog()
            .setHeadBufferSize(256) // make head small enough that it reaches its limit
            .setSequenceGenerator(sequenceGenerator)
            .setCommitLog(commitLog)
            .setGreenHeadPath(greenHeadPath)
            .setRedHeadPath(redHeadPath)
    ) {
      log.open();
      contentList.forEach(log::append);
    }

    try (
        TransactionLog redLogAfterOperation = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(redHeadPath);
        TransactionLog greenLogAfterOperation = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(greenHeadPath);
        TransactionLog commitLogAfterOperation = new TransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath)
    ) {
      List<Content> redLog = redLogAfterOperation.read().toList();
      List<Content> greenLog = greenLogAfterOperation.read().toList();
      List<Content> commitLog = commitLogAfterOperation.read().toList();

      assertTrue(redLog.isEmpty()); // everything was commited to the commitLog
      assertTrue(greenLog.containsAll(contentList.subList(2, 3)));  // overflow that triggered the head switch
      assertTrue(commitLog.containsAll(contentList.subList(0, 2))); // contents of red log
    }
  }


  @AfterAll
  static void afterAll() throws IOException {
    cleanupTestDirectories();
  }

}
