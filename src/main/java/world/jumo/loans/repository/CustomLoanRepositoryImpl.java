package world.jumo.loans.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import world.jumo.loans.domain.LoanAggregate;

@Repository
public class CustomLoanRepositoryImpl implements CustomLoanRepository {

    private final Logger log = LoggerFactory.getLogger(CustomLoanRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<LoanAggregate> calculateLoansAggregate(List<Long> ids) {
        TypedQuery<LoanAggregate> query = entityManager.createQuery("SELECT NEW world.jumo.loans.domain.LoanAggregate(ln.network, ln.product, MONTHNAME(ln.loanDate) as month, SUM(ln.amount) as totalAmount, COUNT(*) as totalLoans)"
                + " FROM Loan ln"
                + " WHERE ln.id in (:ids)"
                + " GROUP BY ln.network, ln.product, MONTHNAME(ln.loanDate)"
                + " ORDER BY ln.network, ln.product, month", LoanAggregate.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}
