package zems.core.contentbus.value;

public record ReferenceValue(String value) implements Value<String> {

  public ReferenceValue() {
    this("");
  }

}
