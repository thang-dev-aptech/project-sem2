package com.example.gympro.service;

import com.example.gympro.viewModel.Invoice;
import java.util.List;

/**
 * Interface (Hợp đồng) cho PaymentService.
 */
public interface PaymentServiceInterface {

    /**
     * Lấy danh sách các hóa đơn chưa thanh toán
     */
    List<Invoice> getUnpaidInvoices();

    /**
     * Xử lý logic nghiệp vụ: Tạo Payment VÀ cập nhật Subscription
     */
    boolean processPayment(
            Invoice invoice,
            long paymentMethodId,
            long createdByUserId
    );
}