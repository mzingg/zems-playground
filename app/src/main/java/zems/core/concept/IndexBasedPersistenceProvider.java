package zems.core.concept;

import java.util.stream.Stream;

public interface IndexBasedPersistenceProvider extends ReadOnlyPersistenceProvider {

    void reset();

    void update(Stream<Content> contentStream);

}
