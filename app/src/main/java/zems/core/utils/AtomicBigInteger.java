package zems.core.utils;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicBigInteger {

    private final AtomicReference<BigInteger> bigInteger;

    public AtomicBigInteger() {
        this(BigInteger.ZERO);
    }

    public AtomicBigInteger(final BigInteger bigInteger) {
        this.bigInteger = new AtomicReference<>(Objects.requireNonNull(bigInteger));
    }

    public void increment() {
        bigInteger.accumulateAndGet(BigInteger.ONE, BigInteger::add);
    }

    public void incrementBy(long amount) {
        bigInteger.accumulateAndGet(BigInteger.valueOf(amount), BigInteger::add);
    }

    public BigInteger get() {
        return bigInteger.get();
    }

    @Override
    public String toString() {
        return bigInteger.toString();
    }

    public AtomicBigInteger set(BigInteger value) {
        bigInteger.set(value);
        return this;
    }
}
