package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRep extends JpaRepository<Transaction, Integer> {

    Optional<Transaction> findById(Long id);
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByAdminId(Long adminId);
    List<Transaction> findByAdminIdAndUserId(Long adminId, Long userId);
    List<Transaction> findByTransactionDate(LocalDateTime transactionDate);

}
