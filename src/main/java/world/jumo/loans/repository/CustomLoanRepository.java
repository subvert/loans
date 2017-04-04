package world.jumo.loans.repository;

import java.util.List;
import world.jumo.loans.domain.LoanAggregate;

public interface CustomLoanRepository {

    public List<LoanAggregate> calculateLoansAggregate(List<Long> ids);
}
