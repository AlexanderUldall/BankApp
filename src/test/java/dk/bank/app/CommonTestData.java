package dk.bank.app;

import dk.bank.app.db.entities.BankAccountEntity;
import dk.bank.app.db.entities.CustomerEntity;
import dk.bank.app.db.entities.TransactionEntity;
import org.openapitools.model.AccountType;
import org.openapitools.model.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommonTestData {

    public static CustomerEntity customerEntity() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName("Test Customer");
        customerEntity.setCustomerId(1L);
        customerEntity.setSocialSecurityNumber("123456-1234");
        customerEntity.setCreationDateTime(LocalDateTime.now());
        return customerEntity;
    }

    public static BankAccountEntity bankAccountEntity() {
        BankAccountEntity bankAccountEntity = new BankAccountEntity();
        bankAccountEntity.setACCOUNT_TYPE(AccountType.CHE);
        bankAccountEntity.setBankAccountId(1L);
        bankAccountEntity.setBalance(new BigDecimal("100"));
        bankAccountEntity.setCustomerId(1L);
        bankAccountEntity.setCreationDateTime(LocalDateTime.now());
        bankAccountEntity.setCurrencyCode(CurrencyCode.DKK);
        return bankAccountEntity;
    }

    public static TransactionEntity transactionEntity() {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(new BigDecimal("100"));
        transactionEntity.setCurrencyCode(CurrencyCode.DKK);
        transactionEntity.setTransactionId(1L);
        transactionEntity.setCreationDateTime(LocalDateTime.now());
        transactionEntity.setBankAccountId(1L);
        return transactionEntity;
    }

    public static List<TransactionEntity> transactionEntityList() {
        List<TransactionEntity> transactionEntities = new ArrayList<>();
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(new BigDecimal("100"));
        transactionEntity.setCurrencyCode(CurrencyCode.DKK);
        transactionEntity.setTransactionId(1L);
        transactionEntity.setCreationDateTime(LocalDateTime.now());
        transactionEntity.setBankAccountId(1L);

        transactionEntities.add(transactionEntity);
        transactionEntities.add(transactionEntity);
        transactionEntities.add(transactionEntity);
        transactionEntities.add(transactionEntity);
        transactionEntities.add(transactionEntity);
        transactionEntities.add(transactionEntity);

        return transactionEntities;
    }
}
