package zems.core.concept;

public interface TransactionLogStatistics {

    default TransactionLogStatistics reset() {
        return this;
    }

    default TransactionLogStatistics countAppendError() {
        return this;
    }

    default TransactionLogStatistics countAppend() {
        return this;
    }

    default TransactionLogStatistics countAppendSegments(int segmentCounts) {
        return this;
    }

    default TransactionLogStatistics countAppendAmountInBytes(int amountInBytes) {
        return this;
    }

    default TransactionLogStatistics countCommitStarted() {
        return this;
    }

    default TransactionLogStatistics countCommitFinished() {
        return this;
    }

    default TransactionLogStatistics countCommitContent() {
        return this;
    }

    default TransactionLogStatistics countCommitError() {
        return this;
    }

    default TransactionLogStatistics countCommitFileCleanupError() {
        return this;
    }

    default TransactionLogStatistics countCommitAmountInBytes(long amountInBytes) {
        return this;
    }


}
