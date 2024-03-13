package com.banking.banking.controller;

import com.banking.banking.entity.Transaction;
import com.banking.banking.service.implementation.BankStatementService;
import com.itextpdf.text.DocumentException;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
@CrossOrigin("*")
public class TransactionalController {
    @Autowired
    private BankStatementService bankStatementService;

    @GetMapping()
    private List<Transaction> generateBankStatement(@RequestParam String accountNumber,@RequestParam String startDate,@RequestParam String endDate) throws DocumentException, FileNotFoundException, MessagingException {
        return bankStatementService.generateStatement(accountNumber, startDate, endDate);
    }

}
