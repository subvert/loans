package world.jumo.loans.web.rest;

import com.codahale.metrics.annotation.Timed;
import world.jumo.loans.domain.Loan;

import world.jumo.loans.repository.LoanRepository;
import world.jumo.loans.web.rest.util.HeaderUtil;
import world.jumo.loans.web.rest.util.PaginationUtil;
import world.jumo.loans.service.dto.LoanDTO;
import world.jumo.loans.service.mapper.LoanMapper;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.springframework.web.multipart.MultipartFile;
import world.jumo.loans.service.LoanService;

/**
 * REST controller for managing Loan.
 */
@RestController
@RequestMapping("/api")
public class LoanResource {

    private final Logger log = LoggerFactory.getLogger(LoanResource.class);

    private static final String ENTITY_NAME = "loan";

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;
    
    private final LoanService loanService;

    public LoanResource(LoanRepository loanRepository, LoanMapper loanMapper, LoanService loanService) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.loanService = loanService;
    }

    /**
     * POST /loans : Upload loans file.
     *
     * @param file
     * @return
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/loans")
    @Timed
    public ResponseEntity<LoanDTO> uploadLoan(@RequestParam(value = "file") MultipartFile file) throws URISyntaxException {
        log.debug("REST request to upload Loans : {}", file.getOriginalFilename());
        
        try {
            loanService.processFile(file);
        } catch (Exception ex) {
            log.debug("Unable to upload file: {}", file.getOriginalFilename());
        }

        return ResponseEntity.created(new URI("/api/loans/"))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, file.getName()))
                .body(null);
    }
    
    /**
     * GET /loans : get all the loans.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of loans in
     * body
     */
    @GetMapping("/loans")
    @Timed
    public ResponseEntity<List<LoanDTO>> getAllLoans(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Loans");
        Page<Loan> page = loanRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/loans");
        return new ResponseEntity<>(loanMapper.loansToLoanDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET /loans/:id : get the "id" loan.
     *
     * @param id the id of the loanDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the
     * loanDTO, or with status 404 (Not Found)
     */
    @GetMapping("/loans/{id}")
    @Timed
    public ResponseEntity<LoanDTO> getLoan(@PathVariable Long id) {
        log.debug("REST request to get Loan : {}", id);
        Loan loan = loanRepository.findOne(id);
        LoanDTO loanDTO = loanMapper.loanToLoanDTO(loan);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(loanDTO));
    }

    /**
     * DELETE /loans/:id : delete the "id" loan.
     *
     * @param id the id of the loanDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/loans/{id}")
    @Timed
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        log.debug("REST request to delete Loan : {}", id);
        loanRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
