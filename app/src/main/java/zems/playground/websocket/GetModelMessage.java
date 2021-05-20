package zems.playground.websocket;

public class GetModelMessage {

  private String clientId;
  private String path;

  public String getClientId() {
    return clientId;
  }

  public GetModelMessage setClientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public String getPath() {
    return path;
  }

  public GetModelMessage setPath(String path) {
    this.path = path;
    return this;
  }

}
