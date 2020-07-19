package tuke.fei;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used for parallel reading from csv file
 */
public class CsvReader {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private Pattern subjectPattern;
    private Pattern organizationPattern;
    private Pattern identificationNumberPattern;
    private Pattern costPattern;
    private Pattern dateOfPublicationPattern;
    private Pattern dateOfIssuePattern;
    private Pattern dueDatePattern;
    private Pattern dateOfDeliveryPattern;
    private Pattern dateOfPaymentPattern;
    private Pattern dateOfCancellationPattern;
    BufferedReader br;

    /**
     * Constructor
     * @param csvPath path to csv
     * @param hasHeader true if csv has header
     * @param subjectPattern pattern for subject
     * @param organizationPattern pattern for organization name
     * @param identificationNumberPattern pattern for organization identification number
     * @param costPattern pattern for cost
     * @param dateOfPublicationPattern pattern for date of publication
     * @param dateOfIssuePattern pattern fo date of issue
     * @param dueDatePattern pattern for due date
     * @param dateOfDeliveryPattern pattern for date of delivery
     * @param dateOfPaymentPattern pattern for date of payment
     * @param dateOfCancellationPattern pattern for date of cancellation
     * @throws IOException
     */
    public CsvReader(
            String csvPath,
            boolean hasHeader,
            Pattern subjectPattern,
            Pattern organizationPattern,
            Pattern identificationNumberPattern,
            Pattern costPattern,
            Pattern dateOfPublicationPattern,
            Pattern dateOfIssuePattern,
            Pattern dueDatePattern,
            Pattern dateOfDeliveryPattern,
            Pattern dateOfPaymentPattern,
            Pattern dateOfCancellationPattern) throws IOException {
        this.subjectPattern = subjectPattern;
        this.organizationPattern = organizationPattern;
        this.identificationNumberPattern = identificationNumberPattern;
        this.costPattern = costPattern;
        this.dateOfPublicationPattern = dateOfPublicationPattern;
        this.dateOfIssuePattern = dateOfIssuePattern;
        this.dueDatePattern = dueDatePattern;
        this.dateOfDeliveryPattern = dateOfDeliveryPattern;
        this.dateOfPaymentPattern = dateOfPaymentPattern;
        this.dateOfCancellationPattern = dateOfCancellationPattern;

        byte[] encoded = Files.readAllBytes(Paths.get(csvPath));
        String file = new String(encoded, StandardCharsets.UTF_8);

        br = new BufferedReader(new FileReader(new File(csvPath)));
        if (hasHeader) {
            br.readLine();
        }
    }

    /**
     * Returns next invoice in csv file
     * @return invoice object, null if end of file
     */
    public synchronized Invoice getNextInvoice() {
        try {
            String line = br.readLine();
            if (line == null) {
                return null;
            }
            Invoice invoice = new Invoice();
            Matcher matcher = subjectPattern.matcher(line);
            matcher.find();
            invoice.setSubject(matcher.group(1));
            matcher = organizationPattern.matcher(line);
            matcher.find();
            invoice.setOrganization(matcher.group(1));
            matcher = identificationNumberPattern.matcher(line);
            matcher.find();
            invoice.setIdentificationNumber(matcher.group(1).trim().isEmpty() ? null : Integer.valueOf(matcher.group(1)));
            matcher = costPattern.matcher(line);
            matcher.find();
            invoice.setCost(Float.valueOf(matcher.group(1).replace("\u20ac", "").replace(",",".").replace(" ", "")));
            matcher = dateOfPublicationPattern.matcher(line);
            matcher.find();
            invoice.setDateOfPublication(matcher.group(1).trim().isEmpty() || matcher.group(1).trim().equalsIgnoreCase("-")? null : dateFormat.parse(matcher.group(1)));
            matcher = dateOfIssuePattern.matcher(line);
            matcher.find();
            invoice.setDateOfIssue(matcher.group(1).trim().isEmpty() || matcher.group(1).trim().equalsIgnoreCase("-")? null : dateFormat.parse(matcher.group(1)));
            matcher = dueDatePattern.matcher(line);
            matcher.find();
            invoice.setDueDate(matcher.group(1).trim().isEmpty() || matcher.group(1).trim().equalsIgnoreCase("-")? null : dateFormat.parse(matcher.group(1)));
            matcher = dateOfDeliveryPattern.matcher(line);
            matcher.find();
            invoice.setDateOfDelivery(matcher.group(1).trim().isEmpty() || matcher.group(1).trim().equalsIgnoreCase("-")? null : dateFormat.parse(matcher.group(1)));
            matcher = dateOfPaymentPattern.matcher(line);
            matcher.find();
            invoice.setDateOfPayment(matcher.group(1).trim().isEmpty() || matcher.group(1).trim().equalsIgnoreCase("-")? null : dateFormat.parse(matcher.group(1)));
            matcher = dateOfCancellationPattern.matcher(line);
            matcher.find();
            invoice.setDateOfCancellation(matcher.group(1).trim().isEmpty() || matcher.group(1).trim().equalsIgnoreCase("-")? null : dateFormat.parse(matcher.group(1)));
            return invoice;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
