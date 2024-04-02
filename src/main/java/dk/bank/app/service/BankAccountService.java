package dk.bank.app.service;

import dk.bank.app.db.entities.BankAccountEntity;
import dk.bank.app.db.entities.CustomerEntity;
import dk.bank.app.db.entities.TransactionEntity;
import dk.bank.app.exceptions.BadRequestException;
import dk.bank.app.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BankAccountService {

    @Autowired
    private DBService dbService;

    public CreateBankAccountResponse createBankAccount(String customerId, final CreateBankAccountRequest createBankAccountRequest) throws BadRequestException {
        BankAccountEntity bankAccount = dbService.createBankAccount(createBankAccountRequest.getCurrencyCode(), createBankAccountRequest.getAccountType(), customerId);

        CreateBankAccountResponse createBankAccountResponse = new CreateBankAccountResponse();
        createBankAccountResponse.setBankAccountID(bankAccount.getBankAccountId());

        return createBankAccountResponse;
    }

    public Customer createCustomer(final CreateCustomerRequest createCustomerRequest) throws BadRequestException {
        CustomerEntity customerEntity = dbService.createCustomer(createCustomerRequest.getSocialSecurityNumber(), createCustomerRequest.getName());

        Customer customer = new Customer();
        customer.setCustomerId(customerEntity.getCustomerId());
        customer.setName(customerEntity.getName());
        customer.setSocialSecurityNumber(customerEntity.getSocialSecurityNumber());

        return customer;
    }

    public Optional<BankAccount> getBankAccount(long customerId, long bankAccountId) {

        Optional<BankAccountEntity> bankAccountEntity = dbService.getBankAccount(customerId, bankAccountId);
        if (bankAccountEntity.isPresent()) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountTYPE(bankAccountEntity.get().getACCOUNT_TYPE());
            bankAccount.setBankAccountId(bankAccountEntity.get().getBankAccountId());
            bankAccount.setBalance(bankAccountEntity.get().getBalance());
            bankAccount.setCurrencyCODE(bankAccountEntity.get().getCurrencyCode());
            return Optional.of(bankAccount);
        } else {
            return Optional.empty();
        }
    }

    public List<BankAccount> getAllBankAccounts(Long customerId) {

        List<BankAccountEntity> bankAccountEntities = dbService.getAllBankAccounts(customerId);
        List<BankAccount> bankAccounts = new ArrayList<>();
        for (BankAccountEntity bankAccountEntity : bankAccountEntities) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountTYPE(bankAccountEntity.getACCOUNT_TYPE());
            bankAccount.setBankAccountId(bankAccountEntity.getBankAccountId());
            bankAccount.setBalance(bankAccountEntity.getBalance());
            bankAccount.setCurrencyCODE(bankAccountEntity.getCurrencyCode());
            bankAccounts.add(bankAccount);
        }
        return bankAccounts;
    }

    public Optional<Customer> getCustomer(String customerId) {

        Optional<CustomerEntity> customerEntity = dbService.getCustomer(customerId);

        if (customerEntity.isPresent()) {
            Customer customer = new Customer();
            customer.setCustomerId(customerEntity.get().getCustomerId());
            customer.setName(customerEntity.get().getName());
            customer.setSocialSecurityNumber(customerEntity.get().getSocialSecurityNumber());
            return Optional.of(customer);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Customer> getCustomerFromSocialSecurityNumber(String socialSecurityNumber) {

        Optional<CustomerEntity> customerEntity = dbService.getCustomerFromSSN(socialSecurityNumber);

        if (customerEntity.isPresent()) {
            Customer customer = new Customer();
            customer.setCustomerId(customerEntity.get().getCustomerId());
            customer.setName(customerEntity.get().getName());
            customer.setSocialSecurityNumber(customerEntity.get().getSocialSecurityNumber());
            return Optional.of(customer);
        } else {
            return Optional.empty();
        }
    }

    public void withdraw(final Long bankAccountId, final TransactionRequest transactionRequest, long customerId) throws NotFoundException, BadRequestException {
        Optional<TransactionEntity> transactionEntity = dbService.withdraw(transactionRequest.getCurrencyCode(), transactionRequest.getAmount(), bankAccountId, customerId);
    }

    public void deposit(final Long bankAccountId, final TransactionRequest transactionRequest, long customerId) throws NotFoundException, BadRequestException {
        Optional<TransactionEntity> transactionEntity = dbService.deposit(transactionRequest.getCurrencyCode(), transactionRequest.getAmount(), bankAccountId, customerId);
    }

    public List<Transaction> get10LatestTransactionsForAccount(long bankAccountId, long customerId) throws NotFoundException, BadRequestException {
        Optional<List<TransactionEntity>> transactionEntities = dbService.get10LatestTransactionsForAccount(bankAccountId, customerId);
        List<Transaction> transactions = new ArrayList<>();

        if (transactionEntities.isPresent()) {
            for (TransactionEntity transactionEntity : transactionEntities.get()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(transactionEntity.getTransactionId());
                transaction.setAmount(transactionEntity.getAmount());
                transaction.setCurrencyCode(transactionEntity.getCurrencyCode());
                transaction.setCreationDateTime(transactionEntity.getCreationDateTime());
                transaction.setBankAccountID(transactionEntity.getBankAccountId());
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public List<Transaction> getAllTransactions(long bankAccountId, long customerId) throws NotFoundException, BadRequestException {
        Optional<List<TransactionEntity>> transactionEntities = dbService.getAllTransactions(bankAccountId, customerId);
        List<Transaction> transactions = new ArrayList<>();

        if (transactionEntities.isPresent()) {
            for (TransactionEntity transactionEntity : transactionEntities.get()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(transactionEntity.getTransactionId());
                transaction.setAmount(transactionEntity.getAmount());
                transaction.setCurrencyCode(transactionEntity.getCurrencyCode());
                transaction.setCreationDateTime(transactionEntity.getCreationDateTime());
                transaction.setBankAccountID(transactionEntity.getBankAccountId());
                transactions.add(transaction);
            }
        }
        return transactions;
    }

}
