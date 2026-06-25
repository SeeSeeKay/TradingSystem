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

import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeServiceInterface {

    private final TradeMapper tradeMapper;
    private final CryptoPairMapper cryptoPairMapper;
    private final CryptoPriceMapper cryptoPriceMapper;
    // Add additional beans here if needed for your implementation

    @Override
    public TradeResponse executeTrade(TradeRequest tradeRequest) {
        // TODO: Implement the core trading engine
        // What should happen when a user executes a trade?

        Long id = cryptoPairMapper.findIdByPairName(tradeRequest.getPairName());
        BigDecimal price = tradeRequest.getTradeType() == TradeType.BUY ? cryptoPriceMapper.findAskPriceByCryptoPairId(id) : cryptoPriceMapper.findBidPriceByCryptoPairId(id);

        throw new UnsupportedOperationException("Trade execution logic to be implemented");
    }
}