package zems.core.properties.conversion;

import java.util.function.Function;

public class ConversionSupport<TARGET> {

  private final Conversion conversion;
  private final Class<TARGET> targetClass;

  public ConversionSupport(Conversion conversion, Class<TARGET> targetClass) {
    this.conversion = conversion;
    this.targetClass = targetClass;
  }

  public <SOURCE> ConversionSupport<TARGET> via(Function<SOURCE, TARGET> conversionFunction) {

    return this;
  }

}
