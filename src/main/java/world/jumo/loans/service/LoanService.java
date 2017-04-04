package world.jumo.loans.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import world.jumo.loans.domain.Loan;
import world.jumo.loans.domain.LoanAggregate;
import world.jumo.loans.repository.CustomLoanRepository;
import world.jumo.loans.repository.LoanRepository;
import world.jumo.loans.utils.Money;

/**
 * Service for managing loans.
 */
@Service
public class LoanService {

    private final Logger log = LoggerFactory.getLogger(LoanService.class);

    private static final String DIRECTORY_NAME = "tmp/";

    //CSV file header
    private static final String[] FILE_HEADER_MAPPING = {"MSISDN", "Network", "Date", "Product", "Amount"};

    //File attributes
    private static final String LOAN_MSISDN = "MSISDN";
    private static final String LOAN_NETWORK = "Network";
    private static final String LOAN_DATE = "Date";
    private static final String LOAN_PRODUCT = "Product";
    private static final String LOAN_AMOUNT = "Amount";
    private static final String LOAN_MONTH = "Month";
    private static final String LOAN_TOTAL_AMOUNT = "TotalAmount";
    private static final String LOAN_TOTAL_LOANS = "TotalLoans";
    private static final String NEW_LINE_SEPARATOR = "\n";
    //CSV result file header
    private static final Object[] FILE_HEADER = {LOAN_NETWORK, LOAN_PRODUCT, LOAN_MONTH, LOAN_TOTAL_AMOUNT, LOAN_TOTAL_LOANS};

    private final LoanRepository loanRepository;
    private final CustomLoanRepository customLoanRepository;

    public LoanService(LoanRepository loanRepository, CustomLoanRepository customLoanRepository) {
        this.loanRepository = loanRepository;
        this.customLoanRepository = customLoanRepository;
    }

    public synchronized void processFile(MultipartFile file) throws Exception {
        FileOutputStream fos = null;
        File directory = new File(DIRECTORY_NAME);

        if (!directory.exists()) {
            // Make sure the file or directory exists and isn't write protected
            if (!directory.mkdir()) {
                if (!directory.mkdirs()) {
                    log.debug("Failed creating directories");
                    throw new Exception("Failed creating directories");
                }
            }
        }

        File convFile = new File(DIRECTORY_NAME + file.getOriginalFilename());
        convFile.createNewFile();
        fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();

        List<Loan> loans = readLoansFile(convFile);

        // Persist loans into database for potential additional calculations
        loans = loanRepository.save(loans);

        List<Long> ids = loans.stream().map(ln -> ln.getId()).collect(Collectors.toList());

        List<LoanAggregate> aggregates = customLoanRepository.calculateLoansAggregate(ids);

        generateLoansResultFile(aggregates, file.getOriginalFilename());
    }

    private List<Loan> readLoansFile(File file) throws Exception {
        Reader in = new FileReader(file);
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING).withQuote('\'');
        CSVParser csvFileParser = new CSVParser(in, csvFileFormat);

        List csvRecords = csvFileParser.getRecords();
        List<Loan> loans = new ArrayList();

        for (int i = 1; i < csvRecords.size(); i++) {
            CSVRecord record = (CSVRecord) csvRecords.get(i);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = sdf.parse(record.get(LOAN_DATE));
            Loan loan = new Loan(Long.parseLong(record.get(LOAN_MSISDN)), record.get(LOAN_NETWORK), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), record.get(LOAN_PRODUCT), Money.valueOf(record.get(LOAN_AMOUNT)).bigDecimalValue());
            loans.add(loan);
        }

        return loans;
    }

    private void generateLoansResultFile(List<LoanAggregate> aggregates, String filename) throws Exception {
        Writer outWriter = null;
        CSVPrinter csvFilePrinter = null;

        //Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            outWriter = new BufferedWriter(new FileWriter(DIRECTORY_NAME + "Output.csv"));

            csvFilePrinter = new CSVPrinter(outWriter, csvFileFormat);
            csvFilePrinter.printRecord(FILE_HEADER);

            for (LoanAggregate aggregate : aggregates) {
                List aggregateDataRecord = new ArrayList();
                aggregateDataRecord.add(aggregate.getNetwork());
                aggregateDataRecord.add(aggregate.getProduct());
                aggregateDataRecord.add(aggregate.getMonth());
                aggregateDataRecord.add(aggregate.getTotalAmount().toString());
                aggregateDataRecord.add(String.valueOf(aggregate.getTotalLoans()));
                csvFilePrinter.printRecord(aggregateDataRecord);
            }
        } catch (Exception e) {
            log.debug("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                csvFilePrinter.close();
            } catch (IOException e) {
                log.debug("Failed to flush filewriter");
            }
        }
    }
}
