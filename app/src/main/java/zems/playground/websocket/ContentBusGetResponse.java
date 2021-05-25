package zems.playground.websocket;

public class ContentBusGetResponse {

  private String clientId;
  private String path;
  private Object properties;

  public String getClientId() {
    return clientId;
  }

  public ContentBusGetResponse setClientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public String getPath() {
    return path;
  }

  public ContentBusGetResponse setPath(String path) {
    this.path = path;
    return this;
  }

  public Object getProperties() {
    return properties;
  }

  public ContentBusGetResponse setProperties(Object properties) {
    this.properties = properties;
    return this;
  }
}
