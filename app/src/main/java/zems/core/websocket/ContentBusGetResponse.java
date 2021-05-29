package zems.core.websocket;

record ContentBusGetResponse(String messageType, String clientId, String path, Object properties) {
}
