package dk.bank.app.Controller;

import dk.bank.app.exceptions.BadRequestException;
import dk.bank.app.exceptions.NotFoundException;
import dk.bank.app.service.BankAccountService;
import org.openapitools.api.BankAccountControllerApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class BankAccountController implements BankAccountControllerApi {

    @Autowired
    private BankAccountService bankAccountService;

    @Override
    public ResponseEntity<CreateBankAccountResponse> createBankAccount(final String customerId, final CreateBankAccountRequest createBankAccountRequest) throws BadRequestException {
        CreateBankAccountResponse bankAccount = bankAccountService.createBankAccount(customerId, createBankAccountRequest);
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Customer> createCustomer(final CreateCustomerRequest createCustomerRequest) throws BadRequestException {
        Customer customer = bankAccountService.createCustomer(createCustomerRequest);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BankAccount> getBankAccount(final Long bankAccountId, final Long customerId) {
        Optional<BankAccount> bankAccount = bankAccountService.getBankAccount(customerId, bankAccountId);
        return bankAccount.map(account -> new ResponseEntity<>(account, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Customer> getCustomer(final String customerId) {
        Optional<Customer> customer = bankAccountService.getCustomer(customerId);
        return customer.map(cust -> new ResponseEntity<>(cust, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Customer> getCustomerFromSSN(final String socialSecurityNumber) throws Exception {
        Optional<Customer> customer = bankAccountService.getCustomerFromSocialSecurityNumber(socialSecurityNumber);
        return customer.map(cust -> new ResponseEntity<>(cust, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Void> withdraw(final Long bankAccountId, final Long customerId, final TransactionRequest transactionRequest) throws BadRequestException, NotFoundException {
        bankAccountService.withdraw(bankAccountId, transactionRequest, customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> deposit(final Long bankAccountId, final Long customerId, final TransactionRequest transactionRequest) throws BadRequestException, NotFoundException {
        bankAccountService.deposit(bankAccountId, transactionRequest, customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<BankAccount>> getAllBankAccounts(final Long customerId) {
        List<BankAccount> allBankAccount = bankAccountService.getAllBankAccounts(customerId);
        return new ResponseEntity<>(allBankAccount, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Transaction>> getAllTransactions(Long bankAccountId, Long customerId) throws Exception {
        List<Transaction> transactions = bankAccountService.getAllTransactions(bankAccountId, customerId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Transaction>> getLatestTransactions(final Long bankAccountId, final Long customerId) throws NotFoundException, BadRequestException {
        List<Transaction> transactions = bankAccountService.get10LatestTransactionsForAccount(bankAccountId, customerId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}
