package zems.core.websocket;

record ContentBusUpdateResponse(String messageType, String changedPath, Object properties) {
}
