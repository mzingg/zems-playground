package zems.core.concept;

public interface ModifiedValue<S, T> extends Value<T> {

    S original();

}
