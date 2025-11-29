package com.example.gympro.repository.billing;

import com.example.gympro.domain.billing.Invoice;
import com.example.gympro.domain.billing.Payment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PaymentRepositoryInterface {
    List<Invoice> findUnpaidInvoices();

    boolean createPayment(Connection conn, Payment payment) throws SQLException;

    boolean updateSubscriptionStatus(Connection conn, long subscriptionId) throws SQLException;

    boolean updateMemberStatus(Connection conn, long subscriptionId) throws SQLException;
}

