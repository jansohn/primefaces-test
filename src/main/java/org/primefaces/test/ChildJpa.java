package org.primefaces.test;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChildJpa {

    @Id
    private Long id;
    private String stringCol;
    private Long numberCol;
    private BigDecimal decimalCol;
    private Date dateCol;

    @Transient
    private String details;

}
