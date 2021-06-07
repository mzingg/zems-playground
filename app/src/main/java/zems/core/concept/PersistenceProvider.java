package zems.core.concept;

public interface PersistenceProvider<T extends PersistenceProvider<T>> extends ReadOnlyPersistenceProvider {

    T write(Content content);

}
