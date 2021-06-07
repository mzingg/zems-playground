package zems.core.contentbus;

import zems.core.concept.PersistenceProvider;

public interface TransactionalContentBusConfigurer {

    PersistenceProvider<?> persistenceProvider();

}
