package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import com.aquariux.technical.assessment.trade.dto.request.TradeRequest;
import com.aquariux.technical.assessment.trade.dto.response.TradeResponse;
import com.aquariux.technical.assessment.trade.entity.UserWallet;
import com.aquariux.technical.assessment.trade.enums.TradeType;
import com.aquariux.technical.assessment.trade.mapper.CryptoPairMapper;
import com.aquariux.technical.assessment.trade.mapper.CryptoPriceMapper;
import com.aquariux.technical.assessment.trade.mapper.TradeMapper;
import com.aquariux.technical.assessment.trade.mapper.UserWalletMapper;
import com.aquariux.technical.assessment.trade.service.TradeServiceInterface;
import com.aquariux.technical.assessment.trade.entity.CryptoPair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;
import com.aquariux.technical.assessment.trade.service.impl.WalletServiceImpl;
import com.aquariux.technical.assessment.trade.entity.Trade;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeServiceInterface {

    private final TradeMapper tradeMapper;
    private final CryptoPairMapper cryptoPairMapper;
    private final CryptoPriceMapper cryptoPriceMapper;
    private final WalletServiceImpl walletService;
    private final UserWalletMapper walletMapper;
    // Add additional beans here if needed for your implementation

    @Override
    public TradeResponse executeTrade(TradeRequest tradeRequest) {
        // TODO: Implement the core trading engine
        // What should happen when a user executes a trade?
        Long userId = tradeRequest.getUserId();
        Boolean isActive = cryptoPairMapper.findActiveByPairName(tradeRequest.getPairName());
        if(isActive){
            Long id = cryptoPairMapper.findIdByPairName(tradeRequest.getPairName());
            BigDecimal price = tradeRequest.getTradeType() == TradeType.BUY ? cryptoPriceMapper.findAskPriceByCryptoPairId(id) : cryptoPriceMapper.findBidPriceByCryptoPairId(id);
            BigDecimal totalValue = price.multiply(tradeRequest.getAmount());
            Trade trade = createTrade(tradeRequest, id, price);
            try{
                CryptoPair cryptoPair = cryptoPairMapper.findByPairName(tradeRequest.getPairName());
                Map<Long, WalletBalanceResponse> walletResMap = walletService.getUserWalletBalancesMap(userId);
                WalletBalanceResponse baseSymbol = walletResMap.get(cryptoPair.getBaseSymbolId()); // What want to buy or sell
                WalletBalanceResponse quoteSymbol = walletResMap.get(cryptoPair.getQuoteSymbolId()); // Currency
                if(tradeRequest.getTradeType() == TradeType.BUY){
                    if(quoteSymbol.getBalance().compareTo(totalValue) < 0){
                        return TradeResponse.builder()
                                .pairName(tradeRequest.getPairName())
                                .tradeType(tradeRequest.getTradeType().toString())
                                .quantity(tradeRequest.getAmount())
                                .executionPrice(price)
                                .totalAmount(totalValue)
                                .status("FAILED")
                                .message(String.format(
                                    "Insufficient %s balance. Required: %.8f, Available: %.8f",
                                    quoteSymbol.getName(), totalValue, quoteSymbol.getBalance()))
                                .tradeTime(LocalDateTime.now())
                                .build();
                    }
                    else{
                        BigDecimal baseBalance = baseSymbol.getBalance().add(tradeRequest.getAmount());
                        BigDecimal quoteBalance = quoteSymbol.getBalance().subtract(totalValue);
                        walletMapper.updateBalance(userId, quoteSymbol.getSymbol(), quoteBalance);
                        walletMapper.updateBalance(userId, baseSymbol.getSymbol(), baseBalance);
                    }
                }
                else{
                    if(baseSymbol.getBalance().compareTo(tradeRequest.getAmount()) < 0){
                        return TradeResponse.builder()
                                .pairName(tradeRequest.getPairName())
                                .tradeType(tradeRequest.getTradeType().toString())
                                .quantity(tradeRequest.getAmount())
                                .executionPrice(price)
                                .totalAmount(totalValue)
                                .status("FAILED")
                                .message(String.format(
                                    "Insufficient %s balance. Required: %.8f, Available: %.8f",
                                    baseSymbol.getName(), tradeRequest.getAmount(), baseSymbol.getBalance()))
                                .tradeTime(LocalDateTime.now())
                                .build();
                    }
                    else{
                        BigDecimal baseBalance = baseSymbol.getBalance().subtract(tradeRequest.getAmount());
                        BigDecimal quoteBalance = quoteSymbol.getBalance().add(totalValue);
                        walletMapper.updateBalance(userId, quoteSymbol.getSymbol(), quoteBalance);
                        walletMapper.updateBalance(userId, baseSymbol.getSymbol(), baseBalance);
                    }
                }
            } catch(Exception ex){
                log.error("Wallet update failed for userId={} pair={}: {}",userId, tradeRequest.getPairName(), ex.getMessage(), ex);
                throw new RuntimeException("Trade failed due to a system error. Please try again.", ex);
            }
            tradeMapper.insertTrade(trade);

            return TradeResponse.builder()
                    .tradeId(trade.getId())
                    .pairName(tradeRequest.getPairName())
                    .tradeType(tradeRequest.getTradeType().toString())
                    .quantity(tradeRequest.getAmount())
                    .executionPrice(price)
                    .totalAmount(totalValue)
                    .status("COMPLETED")
                    .message("Trade executed successfully")
                    .tradeTime(trade.getTradeTime())
                    .build();
        }
        else{
            return TradeResponse.builder()
                .pairName(tradeRequest.getPairName())
                .tradeType(tradeRequest.getTradeType().toString())
                .status("FAILED")
                .message("Trading pair '" + tradeRequest.getPairName() + "' is not active")
                .build();
        }
    }

    private Trade createTrade(TradeRequest tradeRequest, Long cryptoPairId, BigDecimal price){
        Trade trade = new Trade();
        trade.setUserId(tradeRequest.getUserId());
        trade.setCryptoPairId(cryptoPairId);
        trade.setTradeType(tradeRequest.getTradeType().toString());
        trade.setQuantity(tradeRequest.getAmount());
        trade.setPrice(price);
        trade.setTotalAmount(tradeRequest.getAmount());
        trade.setTradeTime(LocalDateTime.now());
        return trade;
    }


}