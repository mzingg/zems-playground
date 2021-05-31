package zems.core.contentbus.value;

public interface DeletedValue<SOURCE_TYPE> extends Value<VoidType> {

  SOURCE_TYPE original();

  @Override
  default VoidType value() {
    return VoidType.NOTHING;
  }
}
