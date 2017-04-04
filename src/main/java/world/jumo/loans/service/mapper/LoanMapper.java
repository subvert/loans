package world.jumo.loans.service.mapper;

import world.jumo.loans.domain.*;
import world.jumo.loans.service.dto.LoanDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Loan and its DTO LoanDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LoanMapper {

    LoanDTO loanToLoanDTO(Loan loan);

    List<LoanDTO> loansToLoanDTOs(List<Loan> loans);

    Loan loanDTOToLoan(LoanDTO loanDTO);

    List<Loan> loanDTOsToLoans(List<LoanDTO> loanDTOs);
    /**
     * generating the fromId for all mappers if the databaseType is sql, as the class has relationship to it might need it, instead of
     * creating a new attribute to know if the entity has any relationship from some other entity
     *
     * @param id id of the entity
     * @return the entity instance
     */
     
    default Loan loanFromId(Long id) {
        if (id == null) {
            return null;
        }
        Loan loan = new Loan();
        loan.setId(id);
        return loan;
    }
    

}
