package world.jumo.loans.web.rest;

import world.jumo.loans.LoansApp;

import world.jumo.loans.domain.Loan;
import world.jumo.loans.repository.LoanRepository;
import world.jumo.loans.service.dto.LoanDTO;
import world.jumo.loans.service.mapper.LoanMapper;
import world.jumo.loans.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import world.jumo.loans.service.LoanService;

/**
 * Test class for the LoanResource REST controller.
 *
 * @see LoanResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoansApp.class)
public class LoanResourceIntTest {

    private static final Long DEFAULT_MSISDN = 1L;
    private static final Long UPDATED_MSISDN = 2L;

    private static final String DEFAULT_NETWORK = "AAAAAAAAAA";
    private static final String UPDATED_NETWORK = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_LOAN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LOAN_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PRODUCT = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private LoanService loanService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLoanMockMvc;

    private Loan loan;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LoanResource loanResource = new LoanResource(loanRepository, loanMapper, loanService);
        this.restLoanMockMvc = MockMvcBuilders.standaloneSetup(loanResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loan createEntity(EntityManager em) {
        Loan loan = new Loan()
            .msisdn(DEFAULT_MSISDN)
            .network(DEFAULT_NETWORK)
            .loanDate(DEFAULT_LOAN_DATE)
            .product(DEFAULT_PRODUCT)
            .amount(DEFAULT_AMOUNT);
        return loan;
    }

    @Before
    public void initTest() {
        loan = createEntity(em);
    }

    @Test
    @Transactional
    public void createLoan() throws Exception {
        int databaseSizeBeforeCreate = loanRepository.findAll().size();

        // Create the Loan
        LoanDTO loanDTO = loanMapper.loanToLoanDTO(loan);
        restLoanMockMvc.perform(post("/api/loans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isCreated());

        // Validate the Loan in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeCreate + 1);
        Loan testLoan = loanList.get(loanList.size() - 1);
        assertThat(testLoan.getMsisdn()).isEqualTo(DEFAULT_MSISDN);
        assertThat(testLoan.getNetwork()).isEqualTo(DEFAULT_NETWORK);
        assertThat(testLoan.getLoanDate()).isEqualTo(DEFAULT_LOAN_DATE);
        assertThat(testLoan.getProduct()).isEqualTo(DEFAULT_PRODUCT);
        assertThat(testLoan.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    public void createLoanWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = loanRepository.findAll().size();

        // Create the Loan with an existing ID
        loan.setId(1L);
        LoanDTO loanDTO = loanMapper.loanToLoanDTO(loan);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLoanMockMvc.perform(post("/api/loans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllLoans() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList
        restLoanMockMvc.perform(get("/api/loans?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loan.getId().intValue())))
            .andExpect(jsonPath("$.[*].msisdn").value(hasItem(DEFAULT_MSISDN.intValue())))
            .andExpect(jsonPath("$.[*].network").value(hasItem(DEFAULT_NETWORK.toString())))
            .andExpect(jsonPath("$.[*].loanDate").value(hasItem(DEFAULT_LOAN_DATE.toString())))
            .andExpect(jsonPath("$.[*].product").value(hasItem(DEFAULT_PRODUCT.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())));
    }

    @Test
    @Transactional
    public void getLoan() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get the loan
        restLoanMockMvc.perform(get("/api/loans/{id}", loan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(loan.getId().intValue()))
            .andExpect(jsonPath("$.msisdn").value(DEFAULT_MSISDN.intValue()))
            .andExpect(jsonPath("$.network").value(DEFAULT_NETWORK.toString()))
            .andExpect(jsonPath("$.loanDate").value(DEFAULT_LOAN_DATE.toString()))
            .andExpect(jsonPath("$.product").value(DEFAULT_PRODUCT.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingLoan() throws Exception {
        // Get the loan
        restLoanMockMvc.perform(get("/api/loans/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLoan() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);
        int databaseSizeBeforeUpdate = loanRepository.findAll().size();

        // Update the loan
        Loan updatedLoan = loanRepository.findOne(loan.getId());
        updatedLoan
            .msisdn(UPDATED_MSISDN)
            .network(UPDATED_NETWORK)
            .loanDate(UPDATED_LOAN_DATE)
            .product(UPDATED_PRODUCT)
            .amount(UPDATED_AMOUNT);
        LoanDTO loanDTO = loanMapper.loanToLoanDTO(updatedLoan);

        restLoanMockMvc.perform(put("/api/loans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isOk());

        // Validate the Loan in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeUpdate);
        Loan testLoan = loanList.get(loanList.size() - 1);
        assertThat(testLoan.getMsisdn()).isEqualTo(UPDATED_MSISDN);
        assertThat(testLoan.getNetwork()).isEqualTo(UPDATED_NETWORK);
        assertThat(testLoan.getLoanDate()).isEqualTo(UPDATED_LOAN_DATE);
        assertThat(testLoan.getProduct()).isEqualTo(UPDATED_PRODUCT);
        assertThat(testLoan.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void updateNonExistingLoan() throws Exception {
        int databaseSizeBeforeUpdate = loanRepository.findAll().size();

        // Create the Loan
        LoanDTO loanDTO = loanMapper.loanToLoanDTO(loan);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLoanMockMvc.perform(put("/api/loans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isCreated());

        // Validate the Loan in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLoan() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);
        int databaseSizeBeforeDelete = loanRepository.findAll().size();

        // Get the loan
        restLoanMockMvc.perform(delete("/api/loans/{id}", loan.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Loan.class);
    }
}
