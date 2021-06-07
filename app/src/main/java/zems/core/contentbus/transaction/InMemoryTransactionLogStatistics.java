package zems.core.contentbus.transaction;

import zems.core.concept.TransactionLogStatistics;
import zems.core.utils.AtomicBigInteger;

import java.math.BigInteger;
import java.util.StringJoiner;

public class InMemoryTransactionLogStatistics implements TransactionLogStatistics {

    AtomicBigInteger appendErrorCount = new AtomicBigInteger();
    AtomicBigInteger appendCount = new AtomicBigInteger();
    AtomicBigInteger appendSegmentCount = new AtomicBigInteger();
    AtomicBigInteger appendBytes = new AtomicBigInteger();
    AtomicBigInteger commitStartedCount = new AtomicBigInteger();
    AtomicBigInteger commitFinishedCount = new AtomicBigInteger();
    AtomicBigInteger commitContentCount = new AtomicBigInteger();
    AtomicBigInteger commitErrorCount = new AtomicBigInteger();
    AtomicBigInteger commitFileCleanupErrorCount = new AtomicBigInteger();
    AtomicBigInteger commitBytes = new AtomicBigInteger();
    AtomicBigInteger headSwitchCount = new AtomicBigInteger();
    AtomicBigInteger headSwitchErrorCount = new AtomicBigInteger();
    AtomicBigInteger headCommitBytes = new AtomicBigInteger();

    @Override
    public TransactionLogStatistics reset() {
        appendErrorCount.set(BigInteger.ZERO);
        appendCount.set(BigInteger.ZERO);
        appendSegmentCount.set(BigInteger.ZERO);
        appendBytes.set(BigInteger.ZERO);
        commitStartedCount.set(BigInteger.ZERO);
        commitFinishedCount.set(BigInteger.ZERO);
        commitContentCount.set(BigInteger.ZERO);
        commitErrorCount.set(BigInteger.ZERO);
        commitFileCleanupErrorCount.set(BigInteger.ZERO);
        commitBytes.set(BigInteger.ZERO);
        headSwitchCount.set(BigInteger.ZERO);
        headSwitchErrorCount.set(BigInteger.ZERO);
        headCommitBytes.set(BigInteger.ZERO);
        
        return this;
    }

    @Override
    public TransactionLogStatistics countAppendError() {
        appendErrorCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countAppend() {
        appendCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countAppendSegments(int segmentCounts) {
        appendSegmentCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countAppendAmountInBytes(int amountInBytes) {
        appendBytes.incrementBy(amountInBytes);
        return this;
    }

    @Override
    public TransactionLogStatistics countCommitStarted() {
        commitStartedCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countCommitFinished() {
        commitFinishedCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countCommitContent() {
        commitContentCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countCommitError() {
        commitErrorCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countCommitFileCleanupError() {
        commitFileCleanupErrorCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countCommitAmountInBytes(long amountInBytes) {
        commitBytes.incrementBy(amountInBytes);
        return this;
    }

    @Override
    public TransactionLogStatistics countHeadSwitch() {
        headSwitchCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countHeadSwitchError() {
        headSwitchErrorCount.increment();
        return this;
    }

    @Override
    public TransactionLogStatistics countHeadCommitAmountInBytes(int amountInBytes) {
        headCommitBytes.incrementBy(amountInBytes);
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", InMemoryTransactionLogStatistics.class.getSimpleName() + "[", "]")
          .add("appendErrorCount=" + appendErrorCount)
          .add("appendCount=" + appendCount)
          .add("appendSegmentCount=" + appendSegmentCount)
          .add("appendBytes=" + appendBytes)
          .add("commitStartedCount=" + commitStartedCount)
          .add("commitFinishedCount=" + commitFinishedCount)
          .add("commitContentCount=" + commitContentCount)
          .add("commitErrorCount=" + commitErrorCount)
          .add("commitFileCleanupErrorCount=" + commitFileCleanupErrorCount)
          .add("commitBytes=" + commitBytes)
          .add("headSwitchCount=" + headSwitchCount)
          .add("headSwitchErrorCount=" + headSwitchErrorCount)
          .add("headCommitBytes=" + headCommitBytes)
          .toString();
    }
}
