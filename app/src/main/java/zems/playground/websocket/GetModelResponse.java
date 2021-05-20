package zems.playground.websocket;

public class GetModelResponse {

  private String clientId;
  private String path;
  private Object properties;

  public String getClientId() {
    return clientId;
  }

  public GetModelResponse setClientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public String getPath() {
    return path;
  }

  public GetModelResponse setPath(String path) {
    this.path = path;
    return this;
  }

  public Object getProperties() {
    return properties;
  }

  public GetModelResponse setProperties(Object properties) {
    this.properties = properties;
    return this;
  }
}
