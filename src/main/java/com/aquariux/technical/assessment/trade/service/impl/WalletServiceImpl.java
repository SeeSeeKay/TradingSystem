package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;
import com.aquariux.technical.assessment.trade.mapper.UserWalletMapper;
import com.aquariux.technical.assessment.trade.service.WalletServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletServiceInterface {

    private final UserWalletMapper userWalletMapper;

    public List<WalletBalanceResponse> getUserWalletBalances(Long userId) {
        List<UserWalletDto> wallets = userWalletMapper.findByUserId(userId);
        
        return wallets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Map<Long, WalletBalanceResponse> getUserWalletBalancesMap(Long userId) {
        List<UserWalletDto> wallets = userWalletMapper.findByUserId(userId);
        
        return wallets.stream()
            .collect(Collectors.toMap(
                    UserWalletDto::getSymbolId,
                    this::mapToResponse
            ));
    }

    private WalletBalanceResponse mapToResponse(UserWalletDto wallet) {
        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setSymbolId(wallet.getSymbolId());
        response.setSymbol(wallet.getSymbol());
        response.setName(wallet.getName());
        response.setBalance(wallet.getBalance());
        return response;
    }
}