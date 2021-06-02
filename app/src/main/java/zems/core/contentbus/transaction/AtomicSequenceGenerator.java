package zems.core.contentbus.transaction;

import zems.core.concept.SequenceGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicSequenceGenerator implements SequenceGenerator {

  private static final int MINIMUM = 10000000;
  private final AtomicLong current = new AtomicLong(MINIMUM);

  @Override public long next() {
    if (current.get() == Long.MAX_VALUE) {
      current.set(MINIMUM);
    }
    return current.incrementAndGet();
  }

}
