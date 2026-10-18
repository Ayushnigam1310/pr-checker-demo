package com.demo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PaymentService {

    private final Map<Long, BigDecimal> balances = new HashMap<>();

    public BigDecimal balanceOf(long accountId) {
        return balances.getOrDefault(accountId, BigDecimal.ZERO);
    }

    public void credit(long accountId, BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        balances.merge(accountId, amount, BigDecimal::add);
    }
}
