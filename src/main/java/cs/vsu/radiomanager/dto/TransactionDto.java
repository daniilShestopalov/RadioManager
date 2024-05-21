package cs.vsu.radiomanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionDto {

    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long adminId;

    @NotNull
    private Double amount;

    @NotNull
    private LocalDateTime transactionDate;

    private String description;
}
