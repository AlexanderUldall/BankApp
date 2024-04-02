package dk.bank.app.db.repositories;

import dk.bank.app.db.entities.TransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository
        extends CrudRepository<TransactionEntity, Long> {

    List<TransactionEntity> findTop10ByBankAccountIdOrderByCreationDateTimeDesc(long bankAccountId);
    List<TransactionEntity> findAllByBankAccountIdOrderByCreationDateTimeDesc(long bankAccountId);
}
