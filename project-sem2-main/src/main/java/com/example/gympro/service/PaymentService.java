package com.example.gympro.service;

import com.example.gympro.repository.PaymentRepository;
import com.example.gympro.repository.PaymentRepositoryInterface;
import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.Invoice;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// SỬA Ở ĐÂY: Thêm "implements"
public class PaymentService implements PaymentServiceInterface {

    // (Các biến Repository và dbConnection giữ nguyên)
    private final PaymentRepositoryInterface paymentRepository = new PaymentRepository();
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    @Override // SỬA: Thêm @Override
    public List<Invoice> getUnpaidInvoices() {
        return paymentRepository.findUnpaidInvoices();
    }

    @Override // SỬA: Thêm @Override
    public boolean processPayment(Invoice invoice, long paymentMethodId, long shiftId, long createdByUserId) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            boolean paymentCreated = paymentRepository.createPayment(conn, invoice, paymentMethodId, shiftId, createdByUserId);
            boolean subUpdated = paymentRepository.updateSubscriptionStatus(conn, invoice.getSubscriptionId());

            if (paymentCreated && subUpdated) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}