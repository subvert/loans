package world.jumo.loans.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the Loan entity.
 */
public class LoanDTO implements Serializable {

    private Long id;

    private Long msisdn;

    private String network;

    private LocalDate loanDate;

    private String product;

    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
    }
    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    public BigDecimal getAmount() {
        return amount;
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

        LoanDTO loanDTO = (LoanDTO) o;

        if ( ! Objects.equals(id, loanDTO.id)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "LoanDTO{" +
            "id=" + id +
            ", msisdn='" + msisdn + "'" +
            ", network='" + network + "'" +
            ", loanDate='" + loanDate + "'" +
            ", product='" + product + "'" +
            ", amount='" + amount + "'" +
            '}';
    }
}
