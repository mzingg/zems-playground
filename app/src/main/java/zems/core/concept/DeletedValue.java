package zems.core.concept;

public interface DeletedValue<S> extends Value<VoidType> {

    S original();

    @Override
    default VoidType value() {
        return VoidType.INSTANCE;
    }
}
