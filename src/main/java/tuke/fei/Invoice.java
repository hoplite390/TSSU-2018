package tuke.fei;

import lombok.Data;

import java.util.Date;

@Data
public class Invoice {
    private float cost;
    private Integer identificationNumber;
    private String organization;
    private String subject;
    private Date dateOfPublication;
    private Date dateOfIssue;
    private Date dueDate;
    private Date dateOfDelivery;
    private Date dateOfPayment;
    private Date dateOfCancellation;
}
