package zems.playground.api;

public class ResourceType {

  private final String resourceTypeSpec;

  public ResourceType(String resourceTypeSpec) {
    this.resourceTypeSpec = resourceTypeSpec;
  }

  public String name() {
    return resourceTypeSpec.substring(resourceTypeSpec.lastIndexOf('/') + 1);
  }

  public String resourceUri(String suffix) {
    return resourceTypeSpec + "/" + name() + suffix;
  }

}
