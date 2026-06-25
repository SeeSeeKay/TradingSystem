package com.aquariux.technical.assessment.trade.mapper;

import com.aquariux.technical.assessment.trade.entity.CryptoPrice;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.math.BigDecimal;

@Mapper
public interface CryptoPriceMapper {
    
    @Insert("""
            INSERT INTO crypto_prices (crypto_pair_id, bid_price, ask_price, bid_source, ask_source) 
            VALUES (#{cryptoPairId}, #{bidPrice}, #{askPrice}, #{bidSource}, #{askSource})
            """)
    void insertPrice(CryptoPrice cryptoPrice);
    
    @Select("""
            SELECT cp.id as id, cp.crypto_pair_id as cryptoPairId, cp.bid_price as bidPrice, cp.ask_price as askPrice, 
                   cp.bid_source as bidSource, cp.ask_source as askSource, cp.created_at as createdAt, pair.pair_name as pairName 
            FROM crypto_prices cp 
            INNER JOIN crypto_pairs pair ON cp.crypto_pair_id = pair.id 
            WHERE cp.created_at = (SELECT MAX(created_at) FROM crypto_prices cp2 WHERE cp2.crypto_pair_id = cp.crypto_pair_id)
            ORDER BY cp.crypto_pair_id
            """)
    List<CryptoPrice> findLatestPrices();

// user can sell at this price
        @Select("""
                SELECT bid_price FROM crypto_prices WHERE crypto_pair_id = #{id}
                """)
    BigDecimal findBidPriceByCryptoPairId(Long id);

// user can buy at this price
        @Select("""
                        SELECT ask_price FROM crypto_prices WHERE crypto_pair_id = #{id}
                        """)
    BigDecimal findAskPriceByCryptoPairId(Long id);
}