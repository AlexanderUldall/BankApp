package dk.bank.app;

import dk.bank.app.db.repositories.BankAccountRepository;
import dk.bank.app.db.repositories.CustomerRepository;
import dk.bank.app.db.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountRepository bankAccountRepository;
    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    public void createCustomerSuccessfully() throws Exception {

        when(customerRepository.findById(any())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(CommonTestData.customerEntity());
        String payload = "{\"socialSecurityNumber\": \"123456-7891\",\"name\" : \"test user\"}";
        mockMvc.perform(post("/api/customer").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void createCustomerInvalidCustomerAlreadyExists() throws Exception {

        when(customerRepository.findBySocialSecurityNumber(any())).thenReturn(Optional.of(CommonTestData.customerEntity()));
        String payload = "{\"socialSecurityNumber\": \"123456-1234\",\"name\" : \"Test Customer\"}";
        mockMvc.perform(post("/api/customer").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("The provided customer already exists."));
    }

    @Test
    public void createCustomerInvalidSocialSecurityNumber() throws Exception {

        String payload = "{\"socialSecurityNumber\": \"123456\",\"name\" : \"Test Customer\"}";
        mockMvc.perform(post("/api/customer").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void createCustomerInvalidName() throws Exception {

        String payload = "{\"socialSecurityNumber\": \"123456-1234\",\"name\" : \"T\"}";
        mockMvc.perform(post("/api/customer").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void createBankAccountSuccessfully() throws Exception {

        when(customerRepository.findById(any())).thenReturn(Optional.of(CommonTestData.customerEntity()));
        when(bankAccountRepository.save(any())).thenReturn(CommonTestData.bankAccountEntity());
        String payload = "{\"currencyCode\": \"DKK\",\"accountType\" : \"SAV\",\"socialSecurityNumber\" : \"123456-1234\"}";
        mockMvc.perform(post("/api/customer/1/bank-account").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("{\"bankAccountID\":1}"));
    }

    @Test
    public void createBankAccountInvalidSocialSecurityNumber() throws Exception {

        String payload = "{\"currencyCode\": \"DKK\",\"accountType\" : \"SAV\",\"socialSecurityNumber\" : \"123456-123\"}";
        mockMvc.perform(post("/api/customer/1/bank-account").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void createBankAccountInvalidCustomerDoesNotExists() throws Exception {

        when(customerRepository.findById(any())).thenReturn(Optional.empty());
        String payload = "{\"currencyCode\": \"DKK\",\"accountType\" : \"SAV\",\"socialSecurityNumber\" : \"123456-1234\"}";
        mockMvc.perform(post("/api/customer/1/bank-account").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("The provided customer does not exists."));
    }

    @Test
    public void getBankAccountSuccessfully() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));
        mockMvc.perform(get("/api/customer/1/bank-account/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.bankAccountId").value(CommonTestData.bankAccountEntity().getBankAccountId()))
                .andExpect(jsonPath("$.balance").value(CommonTestData.bankAccountEntity().getBalance()))
                .andExpect(jsonPath("$.account_TYPE").value(CommonTestData.bankAccountEntity().getACCOUNT_TYPE().toString()))
                .andExpect(jsonPath("$.currency_CODE").value(CommonTestData.bankAccountEntity().getCurrencyCode().toString()));
    }

    @Test
    public void getBankAccountInvalidBankAccountNotFound() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/bank-account/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void getCustomerSuccessfully() throws Exception {

        when(customerRepository.findById(any())).thenReturn(Optional.of(CommonTestData.customerEntity()));
        mockMvc.perform(get("/api/customer/123456-1234")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.socialSecurityNumber").value(CommonTestData.customerEntity().getSocialSecurityNumber()))
                .andExpect(jsonPath("$.name").value(CommonTestData.customerEntity().getName()));
    }

    @Test
    public void getCustomerInvalidCustomerNotFound() throws Exception {

        when(customerRepository.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/customer/123456-1234")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void getCustomerInvalidSocialSecurityNumber() throws Exception {

        when(customerRepository.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/customer/ssn/123456-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void depositSuccessFully() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));
        when(transactionRepository.save(any())).thenReturn(CommonTestData.transactionEntity());
        String payload = "{\"currencyCode\": \"DKK\",\"amount\" : \"11.00\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/deposit").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    public void depositInvalidAmountIsNegative() throws Exception {

        String payload = "{\"currencyCode\": \"DKK\",\"amount\" : \"-11.00\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/deposit").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void depositInvalidAmountFormat() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));

        String payload = "{\"currencyCode\": \"DKK\",\"amount\" : \"11.0\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/deposit").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("Deposit amount must have 2 decimals"));
    }

    @Test
    public void depositInvalidCurrencyDoesNotMatchAccount() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));
        String payload = "{\"currencyCode\": \"USD\",\"amount\" : \"11.0\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/deposit").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("The provided currency does not match the provided account."));
    }

    @Test
    public void depositInvalidAccountNotFound() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.empty());
        String payload = "{\"currencyCode\": \"USD\",\"amount\" : \"11.0\"}";


        mockMvc.perform(post("/api/customer/1/bank-account/1/deposit").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string("The provided bank account does not exists."));
    }

    @Test
    public void withdrawSuccessFully() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));
        when(transactionRepository.save(any())).thenReturn(CommonTestData.transactionEntity());
        String payload = "{\"currencyCode\": \"DKK\",\"amount\" : \"11.00\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/withdraw").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    public void withdrawInvalidAmountIsNegative() throws Exception {

        String payload = "{\"currencyCode\": \"DKK\",\"amount\" : \"-11.00\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/withdraw").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void withdrawInvalidAmountFormat() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));

        String payload = "{\"currencyCode\": \"DKK\",\"amount\" : \"11.0\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/withdraw").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("Withdraw amount must have 2 decimals"));
    }

    @Test
    public void withdrawInvalidCurrencyDoesNotMatchAccount() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));
        String payload = "{\"currencyCode\": \"USD\",\"amount\" : \"11.0\"}";

        mockMvc.perform(post("/api/customer/1/bank-account/1/withdraw").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("The provided currency does not match the provided account."));
    }

    @Test
    public void withdrawInvalidAccountNotFound() throws Exception {

        when(bankAccountRepository.findById(any())).thenReturn(Optional.empty());
        String payload = "{\"currencyCode\": \"USD\",\"amount\" : \"11.0\"}";


        mockMvc.perform(post("/api/customer/1/bank-account/1/withdraw").content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string("The provided bank account does not exists."));
    }

    @Test
    public void get10LatestTransactionsForAccountSuccessFully() throws Exception {
        when(bankAccountRepository.findById(any())).thenReturn(Optional.of(CommonTestData.bankAccountEntity()));
        when(transactionRepository.findTop10ByBankAccountIdOrderByCreationDateTimeDesc(anyLong())).thenReturn(CommonTestData.transactionEntityList());

        mockMvc.perform(get("/api/customer/1/bank-account/1/transactions/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void get10LatestTransactionsForAccountInvalidAccountDoesNotExist() throws Exception {
        when(bankAccountRepository.findById(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bank-account/1/transactions/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }
}
