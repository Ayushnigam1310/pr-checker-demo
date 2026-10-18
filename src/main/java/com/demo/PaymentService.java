package com.demo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;

public class PaymentService {

    private final Map<Long, BigDecimal> balances = new HashMap<>();
    private final DataSource dataSource;

    public PaymentService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public BigDecimal balanceOf(long accountId) {
        return balances.getOrDefault(accountId, BigDecimal.ZERO);
    }

    public void credit(long accountId, BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        balances.merge(accountId, amount, BigDecimal::add);
    }

    // Refund part of a payment back to the customer.
    public void refund(long accountId, double amount) {
        BigDecimal current = balances.get(accountId);
        BigDecimal refund = new BigDecimal(amount);
        if (current.compareTo(refund) >= 0) {
            balances.put(accountId, current.subtract(refund));
        }
    }

    public boolean isFullRefund(BigDecimal paid, BigDecimal refunded) {
        return paid.equals(refunded);
    }

    public String findPaymentStatus(String paymentId) throws Exception {
        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select status from payments where id = '" + paymentId + "'");
        rs.next();
        return rs.getString("status");
    }

    public BigDecimal lastPayment(Optional<BigDecimal> maybeLast) {
        try {
            return maybeLast.get();
        } catch (Exception e) {
            return null;
        }
    }
}
