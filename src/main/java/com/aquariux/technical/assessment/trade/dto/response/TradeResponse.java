package com.aquariux.technical.assessment.trade.dto.response;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TradeResponse {
    // TODO: What should you return after a trade is executed?
    private Long tradeId;
    private String pairName;
    private String tradeType;     
    private BigDecimal quantity;
    private BigDecimal executionPrice;
    private BigDecimal totalAmount;
    private String status;
    private String message;    
    private LocalDateTime tradeTime;
}