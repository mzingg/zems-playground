package zems.core.contentbus;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryContentBusTest {

  @SuppressWarnings("unchecked")
  @Test
  void getPropertiesWithInitialStateReturnsPageTitle() {
    InMemoryContentBus testObj = new InMemoryContentBus()
        .withInitialState("zems/core/contentbus/initialState.json");

    Object actual = testObj.getProperties("/content/playground/de/de");
    assertTrue(actual instanceof Map);
    assertEquals("Page Title From Contentbus", ((Map<String, Object>)actual).get("pageTitle"));
  }
}