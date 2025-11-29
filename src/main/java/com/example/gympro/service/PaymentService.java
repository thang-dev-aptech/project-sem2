package com.example.gympro.service;

import com.example.gympro.domain.billing.Payment;
import com.example.gympro.mapper.billing.InvoiceMapper;
import com.example.gympro.repository.billing.PaymentRepository;
import com.example.gympro.repository.billing.PaymentRepositoryInterface;
import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.Invoice;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PaymentService implements PaymentServiceInterface {

    private final PaymentRepositoryInterface paymentRepository = new PaymentRepository();

    @Override
    public List<Invoice> getUnpaidInvoices() {
        var invoices = paymentRepository.findUnpaidInvoices();
        return InvoiceMapper.toViewModels(invoices);
    }

    @Override
    public boolean processPayment(Invoice invoice, long paymentMethodId, long createdByUserId) {
        var domainInvoice = InvoiceMapper.toDomain(invoice);
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Payment payment = new Payment.Builder()
                    .invoiceId(domainInvoice.getId())
                    .methodId(paymentMethodId)
                    .paidAmount(domainInvoice.getTotalAmount() != null
                            ? domainInvoice.getTotalAmount()
                            : BigDecimal.valueOf(invoice.getTotalAmount()))
                    .createdBy(createdByUserId)
                    .build();

            boolean paymentCreated = paymentRepository.createPayment(conn, payment);
            boolean subUpdated = domainInvoice.getSubscriptionId() == null
                    || paymentRepository.updateSubscriptionStatus(conn, domainInvoice.getSubscriptionId());
            // Update member status to ACTIVE when payment is completed
            // Use memberId from invoice, not subscriptionId
            boolean memberUpdated = domainInvoice.getMember() == null
                    || paymentRepository.updateMemberStatus(conn, domainInvoice.getMember().getId());

            if (paymentCreated && subUpdated && memberUpdated) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
