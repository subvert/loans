package world.jumo.loans.repository;

import world.jumo.loans.domain.Loan;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Loan entity.
 */
@SuppressWarnings("unused")
public interface LoanRepository extends JpaRepository<Loan,Long> {

}