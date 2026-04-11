package com.example.springaicrud.scheduler;

import com.example.springaicrud.entity
        .BuyingDetails;
import com.example.springaicrud.repository
        .BuyingDetailsRepository;
import com.example.springaicrud.service
        .EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation
        .EnableScheduling;
import org.springframework.scheduling.annotation
        .Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class FineScheduler {

    private final BuyingDetailsRepository
            buyingDetailsRepository;
    private final EmailService emailService;

    // ✅ Run every day at 9:00 AM
    // Check due dates and send reminders
    @Scheduled(cron = "0 0 9 * * *")
    public void checkDueDatesAndSendReminders() {

        log.info("Running due date check...");

        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater =
                today.plusDays(3);

        // Get all active records
        List<BuyingDetails> activeRecords =
                buyingDetailsRepository
                        .findByStatus("ACTIVE");

        for (BuyingDetails buying : activeRecords) {

            if (buying.getDueDate() == null)
                continue;

            LocalDate dueDate = buying.getDueDate();

            // ✅ OVERDUE — send overdue alert
            if (today.isAfter(dueDate)) {
                long daysLate = ChronoUnit.DAYS
                        .between(dueDate, today);

                // Update fine in DB
                BigDecimal fine = BigDecimal
                        .valueOf(daysLate * 10);
                buying.setFineAmount(fine);
                buying.setDaysLate((int) daysLate);
                buying.setStatus("OVERDUE");
                buyingDetailsRepository.save(buying);

                // Send overdue alert email
                emailService
                        .sendOverdueAlertEmail(buying);

                log.info("Overdue: {} - {} days late",
                        buying.getBook().getName(),
                        daysLate);
            }

            // ✅ DUE IN 3 DAYS — send reminder
            else if (!dueDate.isAfter(
                    threeDaysLater)
                    && !today.isAfter(dueDate)) {
                emailService
                        .sendDueDateReminderEmail(
                                buying);

                log.info("Reminder sent: {} due {}",
                        buying.getBook().getName(),
                        dueDate);
            }
        }

        log.info("Due date check complete!");
    }

    // ✅ Run every day at 10:00 AM
    // Update all overdue fines
    @Scheduled(cron = "0 0 10 * * *")
    public void updateOverdueFines() {

        log.info("Updating overdue fines...");

        LocalDate today = LocalDate.now();
        List<BuyingDetails> overdueRecords =
                buyingDetailsRepository
                        .findByStatus("OVERDUE");

        for (BuyingDetails buying : overdueRecords) {
            long daysLate = ChronoUnit.DAYS.between(
                    buying.getDueDate(), today);

            BigDecimal fine =
                    BigDecimal.valueOf(daysLate * 10);

            buying.setFineAmount(fine);
            buying.setDaysLate((int) daysLate);
            buyingDetailsRepository.save(buying);

            log.info("Fine updated: {} = ₹{}",
                    buying.getBook().getName(), fine);
        }
    }
}