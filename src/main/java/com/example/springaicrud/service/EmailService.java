package com.example.springaicrud.service;

import com.example.springaicrud.entity
        .BuyingDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory
        .annotation.Value;
import org.springframework.mail
        .javamail.JavaMailSender;
import org.springframework.mail
        .javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ==========================================
    // ✅ Send Book Issued Email
    // ==========================================
    public void sendBookIssuedEmail(
            BuyingDetails buying) {
        try {
            String to      =
                    buying.getUser().getEmail();
            String subject =
                    "Book Issued Successfully!";
            String body    = """
                <html>
                <body style="font-family:Arial">
                <h2 style="color:#1976D2">
                  📚 Book Issued!
                </h2>
                <p>Dear <b>%s</b>,</p>
                <p>Your book has been issued
                   successfully.</p>
                <table border="1"
                       cellpadding="8"
                       style="border-collapse:
                              collapse">
                  <tr style="background:#1976D2;
                             color:white">
                    <th>Field</th>
                    <th>Details</th>
                  </tr>
                  <tr>
                    <td>Book Name</td>
                    <td><b>%s</b></td>
                  </tr>
                  <tr>
                    <td>Author</td>
                    <td>%s</td>
                  </tr>
                  <tr>
                    <td>Issue Date</td>
                    <td>%s</td>
                  </tr>
                  <tr style="background:#fff3e0">
                    <td>Due Date</td>
                    <td><b style="color:red">
                      %s
                    </b></td>
                  </tr>
                </table>
                <p style="color:red">
                  ⚠️ Please return the book
                  before due date to avoid fine!
                </p>
                <p>Fine: ₹10 per day after
                   due date</p>
                <br/>
                <p>Thank you,<br/>
                <b>Library Team</b></p>
                </body>
                </html>
                """.formatted(
                    buying.getUser().getName(),
                    buying.getBook().getName(),
                    buying.getBook().getAuthor(),
                    buying.getIssueDate(),
                    buying.getDueDate()
            );

            sendEmail(to, subject, body);
            log.info("Book issued email sent to: {}",
                    to);

        } catch (Exception e) {
            log.error("Email failed: {}",
                    e.getMessage());
        }
    }

    // ==========================================
    // ✅ Send Due Date Reminder Email
    // ==========================================
    public void sendDueDateReminderEmail(
            BuyingDetails buying) {
        try {
            String to      =
                    buying.getUser().getEmail();
            long daysLeft  = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    buying.getDueDate());
            String subject =
                    "⏰ Book Due Date Reminder - "
                            + buying.getBook().getName();
            String body    = """
                <html>
                <body style="font-family:Arial">
                <h2 style="color:#FF6F00">
                  ⏰ Due Date Reminder!
                </h2>
                <p>Dear <b>%s</b>,</p>
                <p>This is a reminder that your
                   book is due soon!</p>
                <table border="1"
                       cellpadding="8"
                       style="border-collapse:
                              collapse">
                  <tr style="background:#FF6F00;
                             color:white">
                    <th>Field</th>
                    <th>Details</th>
                  </tr>
                  <tr>
                    <td>Book Name</td>
                    <td><b>%s</b></td>
                  </tr>
                  <tr>
                    <td>Due Date</td>
                    <td><b style="color:red">
                      %s
                    </b></td>
                  </tr>
                  <tr style="background:#fff3e0">
                    <td>Days Remaining</td>
                    <td><b style="color:red">
                      %d days left!
                    </b></td>
                  </tr>
                </table>
                <p style="color:red">
                  ⚠️ Please return the book
                  on time to avoid fine of
                  ₹10 per day!
                </p>
                <br/>
                <p>Thank you,<br/>
                <b>Library Team</b></p>
                </body>
                </html>
                """.formatted(
                    buying.getUser().getName(),
                    buying.getBook().getName(),
                    buying.getDueDate(),
                    daysLeft
            );

            sendEmail(to, subject, body);
            log.info("Reminder email sent to: {}",
                    to);

        } catch (Exception e) {
            log.error("Reminder email failed: {}",
                    e.getMessage());
        }
    }

    // ==========================================
    // ✅ Send Overdue Alert Email
    // ==========================================
    public void sendOverdueAlertEmail(
            BuyingDetails buying) {
        try {
            String to        =
                    buying.getUser().getEmail();
            long daysLate    = ChronoUnit.DAYS
                    .between(buying.getDueDate(),
                            LocalDate.now());
            BigDecimal fine  =
                    BigDecimal.valueOf(daysLate * 10);

            String subject =
                    "🚨 Overdue Book Alert - "
                            + buying.getBook().getName();
            String body    = """
                <html>
                <body style="font-family:Arial">
                <h2 style="color:#B71C1C">
                  🚨 Book Overdue Alert!
                </h2>
                <p>Dear <b>%s</b>,</p>
                <p>Your book is OVERDUE!
                   Please return immediately.</p>
                <table border="1"
                       cellpadding="8"
                       style="border-collapse:
                              collapse">
                  <tr style="background:#B71C1C;
                             color:white">
                    <th>Field</th>
                    <th>Details</th>
                  </tr>
                  <tr>
                    <td>Book Name</td>
                    <td><b>%s</b></td>
                  </tr>
                  <tr>
                    <td>Due Date</td>
                    <td>%s</td>
                  </tr>
                  <tr style="background:#ffebee">
                    <td>Days Late</td>
                    <td><b style="color:red">
                      %d days
                    </b></td>
                  </tr>
                  <tr style="background:#ffebee">
                    <td>Fine Amount</td>
                    <td><b style="color:red">
                      ₹%s
                    </b></td>
                  </tr>
                </table>
                <p style="color:red;font-size:16px">
                  ⚠️ Fine increases ₹10 per day!
                  Return ASAP!
                </p>
                <br/>
                <p>Thank you,<br/>
                <b>Library Team</b></p>
                </body>
                </html>
                """.formatted(
                    buying.getUser().getName(),
                    buying.getBook().getName(),
                    buying.getDueDate(),
                    daysLate,
                    fine
            );

            sendEmail(to, subject, body);
            log.info("Overdue alert sent to: {}",
                    to);

        } catch (Exception e) {
            log.error("Overdue email failed: {}",
                    e.getMessage());
        }
    }

    // ==========================================
    // ✅ Send Book Returned Email
    // ==========================================
    public void sendBookReturnedEmail(
            BuyingDetails buying) {
        try {
            String to      =
                    buying.getUser().getEmail();
            String subject =
                    "✅ Book Returned Successfully!";
            String body    = """
                <html>
                <body style="font-family:Arial">
                <h2 style="color:#2E7D32">
                  ✅ Book Returned!
                </h2>
                <p>Dear <b>%s</b>,</p>
                <p>Your book has been returned
                   successfully.</p>
                <table border="1"
                       cellpadding="8"
                       style="border-collapse:
                              collapse">
                  <tr style="background:#2E7D32;
                             color:white">
                    <th>Field</th>
                    <th>Details</th>
                  </tr>
                  <tr>
                    <td>Book Name</td>
                    <td><b>%s</b></td>
                  </tr>
                  <tr>
                    <td>Issue Date</td>
                    <td>%s</td>
                  </tr>
                  <tr>
                    <td>Return Date</td>
                    <td>%s</td>
                  </tr>
                  <tr>
                    <td>Days Late</td>
                    <td>%d days</td>
                  </tr>
                  <tr style="background:#ffebee">
                    <td>Fine Amount</td>
                    <td><b style="color:red">
                      ₹%s
                    </b></td>
                  </tr>
                  <tr>
                    <td>Fine Paid</td>
                    <td>%s</td>
                  </tr>
                </table>
                <br/>
                <p>Thank you for using our
                   library!<br/>
                <b>Library Team</b></p>
                </body>
                </html>
                """.formatted(
                    buying.getUser().getName(),
                    buying.getBook().getName(),
                    buying.getIssueDate(),
                    buying.getReturnDate(),
                    buying.getDaysLate(),
                    buying.getFineAmount(),
                    Boolean.TRUE.equals(
                            buying.getFinePaid())
                            ? "✅ Paid" : "❌ Pending"
            );

            sendEmail(to, subject, body);
            log.info("Return email sent to: {}", to);

        } catch (Exception e) {
            log.error("Return email failed: {}",
                    e.getMessage());
        }
    }

    // ==========================================
    // 🔧 Send Email Helper
    // ==========================================
    private void sendEmail(
            String to,
            String subject,
            String htmlBody)
            throws MessagingException {

        MimeMessage message =
                mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}