package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRep extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByAdminId(Long adminId);
    List<Transaction> findByAdminIdAndUserId(Long adminId, Long userId);
    List<Transaction> findByTransactionDate(LocalDateTime transactionDate);

}
