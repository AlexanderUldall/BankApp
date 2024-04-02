package dk.bank.app.db.repositories;

import dk.bank.app.db.entities.BankAccountEntity;
import dk.bank.app.db.entities.CustomerEntity;
import dk.bank.app.db.entities.TransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository
        extends CrudRepository<BankAccountEntity, Long> {
    List<BankAccountEntity> findAllByCustomerId(long customerId);

}
