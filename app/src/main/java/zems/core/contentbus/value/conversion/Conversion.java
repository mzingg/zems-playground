package zems.core.contentbus.value.conversion;

public class Conversion {

  public <T> ConversionSupport<T> forTarget(Class<T> targetClass) {
    return new ConversionSupport<>(this, targetClass);
  }

}

