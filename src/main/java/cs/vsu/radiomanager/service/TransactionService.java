package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.NameDto;
import cs.vsu.radiomanager.dto.TransactionDto;
import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.mapper.TransactionMapper;
import cs.vsu.radiomanager.model.Transaction;
import cs.vsu.radiomanager.repository.TransactionRep;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRep transactionRep;

    private final UserService userService;

    private final TransactionMapper mapper;

    public List<TransactionDto> getAllTransactions() {
        LOGGER.debug("Fetching all transactions");
        return mapper.toDtoList(transactionRep.findAll());
    }

    public TransactionDto getTransactionById(Long id) {
        LOGGER.debug("Fetching transaction by id: {}", id);
        return transactionRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<TransactionDto> getTransactionsByUserId(Long userId) {
        LOGGER.debug("Fetching transactions by user id: {}", userId);
        return mapper.toDtoList(transactionRep.findByUserId(userId));
    }

    public List<TransactionDto> getTransactionsByAdminId(Long adminId) {
        LOGGER.debug("Fetching transactions by admin id: {}", adminId);
        return mapper.toDtoList(transactionRep.findByAdminId(adminId));
    }

    public List<TransactionDto> getTransactionsAdminIdAndUserId(Long adminId, Long userId) {
        LOGGER.debug("Fetching transactions by admin id: {} and user id: {}", adminId, userId);
        return mapper.toDtoList(transactionRep.findByAdminIdAndUserId(adminId, userId));
    }

    List<TransactionDto> getTransactionsByDate(LocalDateTime transactionDate) {
        LOGGER.debug("Fetching transactions by date {}", transactionDate);
        return mapper.toDtoList(transactionRep.findByTransactionDate(transactionDate));
    }

    public TransactionDto createTransaction(TransactionDto transactionDto) {
        LOGGER.debug("Creating transaction: {}", transactionDto);
        try {
            Transaction newTransaction = transactionRep.save(mapper.toEntity(transactionDto));
            LOGGER.debug("Transaction created: {}", newTransaction);
            return mapper.toDto(newTransaction);
        } catch (Exception e) {
            LOGGER.error("Error while creating transaction", e);
            throw new RuntimeException("Error while creating transaction");
        }
    }

    public boolean deleteTransaction(Long id) {
        LOGGER.debug("Deleting transaction by id: {}", id);
        try {
            Optional<Transaction> transactionOptional = transactionRep.findById(id);
            if (transactionOptional.isPresent()) {
                Transaction transaction = transactionOptional.get();
                transactionRep.delete(transaction);
                LOGGER.debug("Transaction deleted: {}", transaction);
                return true;
            }
            LOGGER.warn("Transaction with id {} not found", id);
            return false;
        } catch (Exception e) {
            LOGGER.error("Error while deleting transaction", e);
            throw new RuntimeException("Error while deleting transaction");
        }
    }

    public NameDto getNameById(Long id) {
        LOGGER.debug("Fetching name by id: {}", id);
        UserDto userDto = userService.getUserById(id);
        if (userDto != null) {
            NameDto nameDto = new NameDto();
            nameDto.setName(userDto.getName());
            nameDto.setSurname(userDto.getSurname());
            return nameDto;
        }
        return null;
    }

}
