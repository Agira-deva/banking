package com.banking.banking.service.implementation;

import com.banking.banking.dto.EmailDetails;
import com.banking.banking.entity.Transaction;
import com.banking.banking.entity.User;
import com.banking.banking.repository.TransactionalRepository;
import com.banking.banking.repository.UserRepository;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Slf4j
public class BankStatementService {


    @Autowired
    private TransactionalRepository transactionalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
     EmailService emailService;

    private static final String file="/home/agira/Downloads/banking/statement/MyStatement.pdf";


    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException, MessagingException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> list = transactionalRepository
                .findByFromAccountAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(accountNumber, start, end).orElseThrow(() -> new RuntimeException("No Transaction found"));
//        List<Transaction> list = transactionalRepository.findAll().stream()
//                .filter(transaction -> transaction.getFromAccount().equals(accountNumber) && transaction.getCreatedAt().isAfter(start) && transaction.getCreatedAt().isBefore(end))
//                .toList();

//        .filter(transaction -> transaction.getCreatedAt().isEqual(start))
//                .filter(transaction -> transaction.getCreatedAt().isEqual(end))

        Optional<User> user = userRepository.existByAccountNumber(accountNumber);
        String customerName=user.get().getFirstName()+" "+user.get().getLastName()+" "+user.get().getOtherName();
        Document document=new Document(PageSize.A4);
        log.info("setting size of document");
        OutputStream outputStream=new FileOutputStream(file);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankInfo=new PdfPTable(1);
        PdfPCell bankName=new PdfPCell(new Phrase("Agira Bank"));
        bankName.setBorder(1);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress= new PdfPCell(new Phrase("St.thomas mount"));
        bankAddress.setBorder(0);
        bankInfo.addCell(bankName);
        bankInfo.addCell(bankAddress);

        PdfPTable statementTable=new PdfPTable(2);
        PdfPCell customerInfo=new PdfPCell(new Phrase("Start date:"+startDate));
        PdfPCell statement=new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate=new PdfPCell(new Phrase("End Date :"+endDate));
        PdfPCell name=new PdfPCell(new Phrase("Customer Name"+customerName));
        name.setBorder(0);

        PdfPCell space=new PdfPCell();
        space.setBorder(0);
        PdfPCell address=new PdfPCell(new Phrase("Address"+user.get().getAddress()));
        address.setBorder(0);

        PdfPTable transactionTable=new PdfPTable(10);
        PdfPCell date=new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType=new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);

        PdfPCell transactionAmount=new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBorder(0);

        PdfPCell status=new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        list.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(String.valueOf(transaction.getAmount())));
            transactionTable.addCell(new Phrase(transaction.getStatus()));

        });
        statementTable.addCell(customerInfo);
        statementTable.addCell(statement);
        statementTable.addCell(endDate);
        statementTable.addCell(name);
        statementTable.addCell(space);
        statementTable.addCell(address);

        document.add(bankInfo);
        document.add(statementTable);
        document.add(transactionTable);
        document.close();

        EmailDetails emailDetails=EmailDetails.builder()
                .recipient(user.get().getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attached")
                .attachment(file)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);


        return list;

    }

    public List<Transaction> getTransactionsByDateRange(String accountNumber, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        return transactionalRepository.findByFromAccountAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(accountNumber, start, end).orElseThrow(() -> new RuntimeException("No Transaction found"));
    }




    }



