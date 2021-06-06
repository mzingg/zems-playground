package zems.core.websocket;

import java.util.Map;

record ContentBusUpdate(String changedPath, Map<String, Object> payload) {
}
