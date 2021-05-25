package zems.playground.websocket;

public class ContentBusGet {

  private String clientId;
  private String path;

  public String getClientId() {
    return clientId;
  }

  public ContentBusGet setClientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  public String getPath() {
    return path;
  }

  public ContentBusGet setPath(String path) {
    this.path = path;
    return this;
  }

}
