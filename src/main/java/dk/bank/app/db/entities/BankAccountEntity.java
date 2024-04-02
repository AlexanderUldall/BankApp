package dk.bank.app.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.model.AccountType;
import org.openapitools.model.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_accounts")
public class BankAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_Account_id")
    private long bankAccountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY_CODE")
    private CurrencyCode currencyCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_TYPE")
    private AccountType ACCOUNT_TYPE;
    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime = LocalDateTime.now();
    @Digits(integer = 10, fraction = 2)
    @Column(name = "balance")
    private BigDecimal balance = new BigDecimal("0.0");
    @Column(name = "customer_id")
    private long customerId;

}