package world.jumo.loans.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class LoanAggregate implements Serializable {

    private String network;

    private String product;

    private String month;

    private BigDecimal totalAmount;

    private Long totalLoans;

    public LoanAggregate(String network, String product, String month, BigDecimal totalAmount, Long totalLoans) {
        this.network = network;
        this.product = product;
        this.month = month;
        this.totalAmount = totalAmount;
        this.totalLoans = totalLoans;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalLoans() {
        return totalLoans;
    }

    public void setTotalLoans(Long totalLoans) {
        this.totalLoans = totalLoans;
    }

    @Override
    public String toString() {
        return "LoanAggregateDTO{"
                + "network='" + network + "'"
                + ", product='" + product + "'"
                + ", month='" + month + "'"
                + ", totalAmount='" + totalAmount + "'"
                + ", totalLoans='" + totalLoans + "'"
                + '}';
    }
}
