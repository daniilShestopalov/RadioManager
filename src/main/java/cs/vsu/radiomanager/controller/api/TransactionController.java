package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.TransactionDto;
import cs.vsu.radiomanager.service.TransactionService;
import cs.vsu.radiomanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transaction")
@AllArgsConstructor
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all transactions",
            description = "Retrieves a list of all transactions."
    )
    public ResponseEntity<?> getAllTransactions() {
        try {
            LOGGER.info("Fetching all transactions");
            List<TransactionDto> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LOGGER.error("Error fetching all transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a transaction by its ID."
    )
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching transaction with ID: {}", id);
            TransactionDto transaction = transactionService.getTransactionById(id);
            if (transaction != null) {
                return ResponseEntity.ok(transaction);
            }

            LOGGER.warn("Transaction with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found.");
        } catch (Exception e) {
            LOGGER.error("Error fetching transaction with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get transactions by user ID",
            description = "Retrieves transactions belonging to a specific user."
    )
    public ResponseEntity<?> getTransactionsByUserId(@PathVariable Long userId) {
        try {
            LOGGER.info("Fetching transactions for user ID: {}", userId);
            List<TransactionDto> transactions = transactionService.getTransactionsByUserId(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LOGGER.error("Error fetching transactions for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get transactions by admin ID",
            description = "Retrieves transactions associated with a specific administrator."
    )
    public ResponseEntity<?> getTransactionsByAdminId(@PathVariable Long adminId) {
        try {
            LOGGER.info("Fetching transactions for admin ID: {}", adminId);
            List<TransactionDto> transactions = transactionService.getTransactionsByAdminId(adminId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LOGGER.error("Error fetching transactions for admin ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin-user")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get transactions by admin and user IDs",
            description = "Retrieves transactions filtered by both administrator ID and user ID. " +
                    "Provide adminId and userId as query parameters."
    )
    public ResponseEntity<?> getTransactionsByAdminUser(@RequestParam Long adminId, @RequestParam Long userId) {
        try {
            LOGGER.info("Fetching transactions for admin ID: {} and user ID: {}", adminId, userId);
            List<TransactionDto> transactions = transactionService.getTransactionsByAdminIdAndUserId(adminId, userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LOGGER.error("Error fetching transactions for admin ID: {} and user ID: {}", adminId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-date")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get transactions by date",
            description = "Retrieves transactions for the specified transaction date."
    )
    public ResponseEntity<?> getTransactionsByDate(@RequestParam("date")
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       LocalDateTime date) {
        try {
            LOGGER.info("Fetching transactions for date: {}", date);
            List<TransactionDto> transactions = transactionService.getTransactionsByDate(date);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            LOGGER.error("Error fetching transactions for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/system")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Create system transaction",
            description = "Creates a system transaction by automatically " +
                    "assigning the system entity's admin ID to the transaction."
    )
    public ResponseEntity<?> createSystemTransaction(@RequestBody @Valid TransactionDto transactionDto) {
        try {
            LOGGER.info("Creating system transaction: {}", transactionDto);
            transactionDto.setAdminId(userService.getSystemEntity().getId());
            TransactionDto created = transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            LOGGER.error("Error creating system transaction: {}", transactionDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create transaction",
            description = "Creates a new transaction."
    )
    public ResponseEntity<?> createTransaction(@RequestBody @Valid TransactionDto transactionDto) {
        try {
            LOGGER.info("Creating transaction: {}", transactionDto);
            TransactionDto created = transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            LOGGER.error("Error creating transaction: {}", transactionDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete transaction",
            description = "Deletes a transaction by its ID."
    )
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            LOGGER.info("Deleting transaction with ID: {}", id);
            boolean deleted = transactionService.deleteTransaction(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }

            LOGGER.warn("Transaction with ID for delete {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error deleting transaction with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
