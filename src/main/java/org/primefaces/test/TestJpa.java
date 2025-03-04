package org.primefaces.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TestJpa {

    @Id
    private Long id;
    private String stringCol;
    private Long numberCol;
    private BigDecimal decimalCol;
    private Date dateCol;
    @OneToMany
    private List<ChildJpa> children;

}
