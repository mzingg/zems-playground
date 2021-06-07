package zems.core.contentbus.persistence;

import org.junit.jupiter.api.Test;
import zems.core.concept.Content;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryPersistenceProviderTest {

    @Test
    void getPropertiesWithInitialStateReturnsPageTitle() {
        InMemoryPersistenceProvider testObj = new InMemoryPersistenceProvider()
          .loadFromClassPath("zems/core/ContentBus/initialState.json");

        Optional<Content> actual = testObj.read("/content/playground/de/de");
        assertTrue(actual.isPresent());
        assertEquals("Page Title From Contentbus", actual.get().properties().get("pageTitle", String.class).orElse(""));
    }
}