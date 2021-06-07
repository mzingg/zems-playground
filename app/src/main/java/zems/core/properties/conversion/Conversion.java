package zems.core.properties.conversion;

public class Conversion {

    public <T> ConversionSupport<T> forTarget(Class<T> targetClass) {
        return new ConversionSupport<>(this, targetClass);
    }

}

