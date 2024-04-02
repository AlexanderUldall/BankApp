package dk.bank.app.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private long customerId;
    @Column(name = "social_security_number")
    private String socialSecurityNumber;
    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime = LocalDateTime.now();
    @Column(name = "name")
    private String name;

}