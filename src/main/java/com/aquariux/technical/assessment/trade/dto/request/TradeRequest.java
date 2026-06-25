package com.aquariux.technical.assessment.trade.dto.request;

import com.aquariux.technical.assessment.trade.enums.TradeType;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class TradeRequest {
    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "tradeType is required")
    private TradeType tradeType;

    @NotNull(message = "symbol is required")
    private String symbol;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private BigDecimal amount;
    
    // TODO: What information do you need to execute a trade?
}