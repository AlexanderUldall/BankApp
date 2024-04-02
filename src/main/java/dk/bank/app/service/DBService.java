package dk.bank.app.service;

import dk.bank.app.db.entities.BankAccountEntity;
import dk.bank.app.db.entities.CustomerEntity;
import dk.bank.app.db.entities.TransactionEntity;
import dk.bank.app.db.repositories.BankAccountRepository;
import dk.bank.app.db.repositories.CustomerRepository;
import dk.bank.app.db.repositories.TransactionRepository;
import dk.bank.app.exceptions.BadRequestException;
import dk.bank.app.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.AccountType;
import org.openapitools.model.CurrencyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class DBService {

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private final int exactDecimalCount = 2;
    private final int maximumIntegerDigitsCount = 10;

    public BankAccountEntity createBankAccount(CurrencyCode currencyCode, AccountType accountType, String customerId) throws BadRequestException {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);
        if (customerEntity.isEmpty()) {
            throw new BadRequestException("The provided customer does not exists.");
        }
        BankAccountEntity bankAccountEntity = new BankAccountEntity();
        bankAccountEntity.setCurrencyCode(currencyCode);
        bankAccountEntity.setACCOUNT_TYPE(accountType);
        bankAccountEntity.setCustomerId(customerEntity.get().getCustomerId());

        return bankAccountRepository.save(bankAccountEntity);
    }

    public CustomerEntity createCustomer(String socialSecurityNumber, String name) throws BadRequestException {

        Optional<CustomerEntity> customerEntity = customerRepository.findBySocialSecurityNumber(socialSecurityNumber);
        if (customerEntity.isPresent()) {
            throw new BadRequestException("The provided customer already exists.");
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setSocialSecurityNumber(socialSecurityNumber);
        customer.setName(name);

        return customerRepository.save(customer);
    }

    public Optional<BankAccountEntity> getBankAccount(long customerId, long bankAccountId) {
        Optional<BankAccountEntity> bankAccountEntity = bankAccountRepository.findById(bankAccountId);

        if (bankAccountEntity.isPresent() && bankAccountEntity.get().getCustomerId() == customerId) {
            return bankAccountEntity;
        } else {
            return Optional.empty();
        }
    }

    public List<BankAccountEntity> getAllBankAccounts(Long customerId) {
        return bankAccountRepository.findAllByCustomerId(customerId);
    }

    public Optional<CustomerEntity> getCustomer(String customerId) {
        return customerRepository.findById(customerId);
    }

    public Optional<CustomerEntity> getCustomerFromSSN(String socialSecurityNumber) {
        return customerRepository.findBySocialSecurityNumber(socialSecurityNumber);
    }

    @Transactional
    public Optional<TransactionEntity> withdraw(CurrencyCode currencyCode, BigDecimal amount, long bankAccountId, long customerId) throws BadRequestException, NotFoundException {

        Optional<BankAccountEntity> bankAccountEntity = bankAccountRepository.findById(bankAccountId);

        if (bankAccountEntity.isEmpty()) {
            throw new NotFoundException("The provided bank account does not exists.");
        }

        if (bankAccountEntity.get().getCustomerId() != customerId) {
            throw new BadRequestException("The provided bank account does not belong to the customer.");
        }

        if (!bankAccountEntity.get().getCurrencyCode().equals(currencyCode)) {
            throw new BadRequestException("The provided currency does not match the provided account.");
        }

        if (countDecimalPlaces(amount) != exactDecimalCount) {
            throw new BadRequestException("Withdraw amount must have 2 decimals");
        }

        BigDecimal totalAmount = bankAccountEntity.get().getBalance().subtract(amount);
        if (countIntegerDigits(totalAmount) > maximumIntegerDigitsCount) {
            throw new BadRequestException("Could not complete withdraw as the account would exceed 10 digits negative balance");
        }

        bankAccountEntity.get().setBalance(totalAmount);
        bankAccountRepository.save(bankAccountEntity.get());

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(amount.negate());
        transactionEntity.setCurrencyCode(currencyCode);
        transactionEntity.setBankAccountId(bankAccountId);

        return Optional.of(transactionRepository.save(transactionEntity));
    }

    @Transactional
    public Optional<TransactionEntity> deposit(CurrencyCode currencyCode, BigDecimal amount, long bankAccountId, long customerId) throws BadRequestException, NotFoundException {

        Optional<BankAccountEntity> bankAccountEntity = bankAccountRepository.findById(bankAccountId);

        if (bankAccountEntity.isEmpty()) {
            throw new NotFoundException("The provided bank account does not exists.");
        }

        if (bankAccountEntity.get().getCustomerId() != customerId) {
            throw new BadRequestException("The provided bank account does not belong to the customer.");
        }

        if (!bankAccountEntity.get().getCurrencyCode().equals(currencyCode)) {
            throw new BadRequestException("The provided currency does not match the provided account.");
        }
        if (countDecimalPlaces(amount) != exactDecimalCount) {
            throw new BadRequestException("Deposit amount must have 2 decimals");
        }

        BigDecimal totalAmount = bankAccountEntity.get().getBalance().add(amount);

        if (countIntegerDigits(totalAmount) > maximumIntegerDigitsCount) {
            throw new BadRequestException("Could not complete deposit as the account balance would exceed 10 digits");
        }

        bankAccountEntity.get().setBalance(totalAmount);
        bankAccountRepository.save(bankAccountEntity.get());

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(amount);
        transactionEntity.setCurrencyCode(currencyCode);
        transactionEntity.setBankAccountId(bankAccountId);

        return Optional.of(transactionRepository.save(transactionEntity));
    }

    public Optional<List<TransactionEntity>> get10LatestTransactionsForAccount(long bankAccountId, long customerId) throws NotFoundException, BadRequestException {

        Optional<BankAccountEntity> bankAccountEntity = bankAccountRepository.findById(bankAccountId);

        if (bankAccountEntity.isEmpty()) {
            throw new NotFoundException("The provided bank account does not exists.");
        }

        if (bankAccountEntity.get().getCustomerId() != customerId) {
            throw new BadRequestException("The provided bank account does not belong to the customer.");
        }

        List<TransactionEntity> transactionEntities = transactionRepository.findTop10ByBankAccountIdOrderByCreationDateTimeDesc(bankAccountId);
        return Optional.of(transactionEntities);
    }

    public Optional<List<TransactionEntity>> getAllTransactions(long bankAccountId, long customerId) throws NotFoundException, BadRequestException {

        Optional<BankAccountEntity> bankAccountEntity = bankAccountRepository.findById(bankAccountId);

        if (bankAccountEntity.isEmpty()) {
            throw new NotFoundException("The provided bank account does not exists.");
        }

        if (bankAccountEntity.get().getCustomerId() != customerId) {
            throw new BadRequestException("The provided bank account does not belong to the customer.");
        }

        List<TransactionEntity> transactionEntities = transactionRepository.findAllByBankAccountIdOrderByCreationDateTimeDesc(bankAccountId);
        return Optional.of(transactionEntities);
    }

    private int countIntegerDigits(BigDecimal n) {
        return n.signum() == 0 ? 1 : n.precision() - n.scale();
    }

    private int countDecimalPlaces(BigDecimal bigDecimal) {
        String string = bigDecimal.toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

    private boolean isValidSocialSecurityNumber(String socialSecurity) {
        Pattern pattern = Pattern.compile("[0-9]{6}-[0-9]{4}");
        return pattern.matcher(socialSecurity).find();
    }

    private boolean isValidName(String name) {
        return name.length() > 1 && name.length() < 101;
    }

    private boolean isPositiveAmount(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) != -1;
    }

}
