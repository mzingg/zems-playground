package zems.core.transaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import zems.core.concept.Content;
import zems.core.concept.SequenceGenerator;
import zems.core.properties.InMemoryProperties;

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
              .setSequenceGenerator(new AtomicSequenceGenerator())
      ) {
        log.append(new Content("/a/path", new InMemoryProperties()));
      }
    });
  }

  @Test
  void openWithoutLogPathSetThrowsException() {
    assertThrows(IllegalStateException.class, () -> {
      try (
          HotTransactionLog log = new HotTransactionLog()
              .setSequenceGenerator(new AtomicSequenceGenerator())
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
              .setSequenceGenerator(new AtomicSequenceGenerator())
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
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    List<Content> contentList = Arrays.asList(
        new Content("/a/path", new InMemoryProperties()),
        new Content("/a/second/path", new InMemoryProperties().put("hallo", "velo")),
        new Content("/a/third/path", new InMemoryProperties().put("third", 1234234))
    );

    try (
        MainTransactionLog commitLog = new MainTransactionLog()
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
        MainTransactionLog redLogAfterOperation = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(redHeadPath);
        MainTransactionLog greenLogAfterOperation = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(greenHeadPath);
        MainTransactionLog commitLogAfterOperation = new MainTransactionLog()
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
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    List<Content> contentList = Arrays.asList(
        new Content("/a/path", new InMemoryProperties()),
        new Content("/a/second/path", new InMemoryProperties().put("hallo", "velo lorem ipsum")),
        new Content("/a/third/path", new InMemoryProperties().put("third", 1234234))
    );

    try (
        MainTransactionLog commitLog = new MainTransactionLog()
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
        MainTransactionLog redLogAfterOperation = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(redHeadPath);
        MainTransactionLog greenLogAfterOperation = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setAllowsSuperfluousData(true)
            .setLogPath(greenHeadPath);
        MainTransactionLog commitLogAfterOperation = new MainTransactionLog()
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

  @Test
  void openWithExistingSwitchedHeadSwitchesToThePreviousUsedHead() throws Exception {
    Path logPath = aTestPath("hot.tn");
    Path greenHeadPath = aTestPath("hot.green.tn");
    Path redHeadPath = aTestPath("hot.red.tn");
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    List<Content> contentList = Arrays.asList(
        new Content("/a/path", new InMemoryProperties()),
        new Content("/a/second/path", new InMemoryProperties().put("hallo", "velo lorem ipsum")),
        new Content("/a/third/path", new InMemoryProperties().put("third", 1234234))
    );

    try (
        MainTransactionLog commitLog = new MainTransactionLog()
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
      contentList.forEach(log::append); // used current head -> green
    }

    // close log and reopen it for the assertion

    try (
        MainTransactionLog commitLog = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath);
        HotTransactionLog log = new HotTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setCommitLog(commitLog)
            .setGreenHeadPath(greenHeadPath)
            .setRedHeadPath(redHeadPath)
    ) {
      log.open();
      Assertions.assertEquals(HotTransactionLog.HeadState.GREEN, log.getState());
    }
  }

  @Test
  void openWithExistingNonSwitchedHeadSwitchesToThePreviousUsedHead() throws Exception {
    Path logPath = aTestPath("hot.tn");
    Path greenHeadPath = aTestPath("hot.green.tn");
    Path redHeadPath = aTestPath("hot.red.tn");
    SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();
    List<Content> contentList = Arrays.asList(
        new Content("/a/path", new InMemoryProperties()),
        new Content("/a/second/path", new InMemoryProperties().put("hallo", "velo lorem ipsum")),
        new Content("/a/third/path", new InMemoryProperties().put("third", 1234234))
    );

    try (
        MainTransactionLog commitLog = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath);
        HotTransactionLog log = new HotTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setCommitLog(commitLog)
            .setGreenHeadPath(greenHeadPath)
            .setRedHeadPath(redHeadPath)
    ) {
      log.open();
      contentList.forEach(log::append); // used current head -> red
    }

    // close log and reopen it for the assertion

    try (
        MainTransactionLog commitLog = new MainTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setLogPath(logPath);
        HotTransactionLog log = new HotTransactionLog()
            .setSequenceGenerator(sequenceGenerator)
            .setCommitLog(commitLog)
            .setGreenHeadPath(greenHeadPath)
            .setRedHeadPath(redHeadPath)
    ) {
      log.open();
      Assertions.assertEquals(HotTransactionLog.HeadState.RED, log.getState());
    }
  }

  @AfterAll
  static void afterAll() throws IOException {
    cleanupTestDirectories();
  }

}
