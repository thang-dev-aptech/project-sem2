package com.example.gympro.repository;

import com.example.gympro.viewModel.Invoice;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PaymentRepositoryInterface {
    List<Invoice> findUnpaidInvoices();
    boolean createPayment(Connection conn, Invoice invoice, long methodId, long shiftId, long userId) throws SQLException;
    boolean updateSubscriptionStatus(Connection conn, long subscriptionId) throws SQLException;
}