package zems.core.concept;

public interface TransactionLog<T extends TransactionLog<T>> extends AutoCloseable {

  T append(Content... content);

  T setSequenceGenerator(SequenceGenerator sequenceGenerator);

}
