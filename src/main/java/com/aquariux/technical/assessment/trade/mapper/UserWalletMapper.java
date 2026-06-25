package com.aquariux.technical.assessment.trade.mapper;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.math.BigDecimal;

@Mapper
public interface UserWalletMapper {
    
    @Select("""
            SELECT s.symbol, s.name, uw.balance 
            FROM symbols s 
            INNER JOIN user_wallets uw ON s.id = uw.symbol_id AND uw.user_id = #{userId} 
            ORDER BY s.symbol
            """)
    List<UserWalletDto> findByUserId(Long userId);
    
    @Update("""
        UPDATE user_wallets uw
        INNER JOIN symbols s ON s.id = uw.symbol_id
        SET uw.balance = #{balance}
        WHERE uw.user_id = #{userId}
        AND s.symbol = #{symbol}
        """)
    void updateBalance(Long userId, String symbol, BigDecimal balance);

}