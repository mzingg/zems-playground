package zems.core.contentbus.value;

public interface ModifiedValue<SOURCE_TYPE, TARGET_TYPE> extends Value<TARGET_TYPE> {

  SOURCE_TYPE original();

}
