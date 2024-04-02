package dk.bank.app.db.repositories;

import dk.bank.app.db.entities.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository
        extends CrudRepository<CustomerEntity, String> {

    Optional<CustomerEntity> findBySocialSecurityNumber(String socialSecurityNumber);

}
