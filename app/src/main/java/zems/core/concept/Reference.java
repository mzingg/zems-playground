package zems.core.concept;

public interface Reference<T> extends Value<String> {

    default boolean isNotResolved() {
        return data() == null;
    }

    Reference<T> resolve(ContentBus contentBus);

    T data();
}
