package com.aquariux.technical.assessment.trade.service;

import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;

import java.util.List;
import java.util.Map;

public interface WalletServiceInterface {
    List<WalletBalanceResponse> getUserWalletBalances(Long userId);
    Map<Long, WalletBalanceResponse> getUserWalletBalancesMap(Long userId);
}