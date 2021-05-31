package zems.core.transaction;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceGenerator {

  private static final int MINIMUM = 10000000;
  private final AtomicLong current = new AtomicLong(MINIMUM);

  public long next() {
    if (current.get() == Long.MAX_VALUE) {
      current.set(MINIMUM);
    }
    return current.incrementAndGet();
  }

}
