package dk.bank.app.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.model.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long transactionId;
    @Column(name = "CURRENCY_CODE")
    private CurrencyCode currencyCode;
    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime = LocalDateTime.now();
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "bank_Account_id")
    private long bankAccountId;

}