package world.jumo.loans.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Loan.
 */
@Entity
@Table(name = "loan")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "msisdn")
    private Long msisdn;

    @Column(name = "network")
    private String network;

    @Column(name = "loan_date")
    private LocalDate loanDate;

    @Column(name = "product")
    private String product;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    public Loan() {
    }

    public Loan(Long msisdn, String network, LocalDate loanDate, String product, BigDecimal amount) {
        this.msisdn = msisdn;
        this.network = network;
        this.loanDate = loanDate;
        this.product = product;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public Loan msisdn(Long msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
    }

    public String getNetwork() {
        return network;
    }

    public Loan network(String network) {
        this.network = network;
        return this;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public Loan loanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
        return this;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public String getProduct() {
        return product;
    }

    public Loan product(String product) {
        this.product = product;
        return this;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Loan amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Loan loan = (Loan) o;
        if (loan.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Loan{"
                + "id=" + id
                + ", msisdn='" + msisdn + "'"
                + ", network='" + network + "'"
                + ", loanDate='" + loanDate + "'"
                + ", product='" + product + "'"
                + ", amount='" + amount + "'"
                + '}';
    }
}
