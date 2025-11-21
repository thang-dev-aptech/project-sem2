package com.example.gympro.service;

import com.example.gympro.repository.PaymentMethodRepository;

import java.util.List;
import java.util.Map;

/**
 * Service để quản lý payment methods
 */
public class PaymentMethodService {

    private final PaymentMethodRepository repository = new PaymentMethodRepository();
    private Map<String, Long> codeToIdCache = null;

    /**
     * Lấy tất cả payment methods
     */
    public List<PaymentMethodRepository.PaymentMethod> getAllPaymentMethods() {
        return repository.findAll();
    }

    /**
     * Lấy payment method theo code (CASH, BANK, QR, CARD)
     */
    public PaymentMethodRepository.PaymentMethod getByCode(String code) {
        return repository.findByCode(code);
    }

    /**
     * Lấy payment method theo ID
     */
    public PaymentMethodRepository.PaymentMethod getById(long id) {
        return repository.findById(id);
    }

    /**
     * Lấy ID của payment method theo code (có cache)
     */
    public long getIdByCode(String code) {
        if (codeToIdCache == null) {
            codeToIdCache = repository.getCodeToIdMap();
        }
        Long id = codeToIdCache.get(code);
        return id != null ? id : 0;
    }

    /**
     * Lấy code của payment method theo ID
     */
    public String getCodeById(long id) {
        PaymentMethodRepository.PaymentMethod method = getById(id);
        return method != null ? method.getCode() : null;
    }

    /**
     * Clear cache (nếu cần refresh)
     */
    public void clearCache() {
        codeToIdCache = null;
    }

    /**
     * Lấy các payment method IDs thường dùng
     */
    public PaymentMethodIds getCommonPaymentMethodIds() {
        return new PaymentMethodIds(
            getIdByCode("CASH"),
            getIdByCode("BANK"),
            getIdByCode("QR"),
            getIdByCode("CARD")
        );
    }

    /**
     * Inner class để chứa các payment method IDs
     */
    public static class PaymentMethodIds {
        private final long cashId;
        private final long bankId;
        private final long qrId;
        private final long cardId;

        public PaymentMethodIds(long cashId, long bankId, long qrId, long cardId) {
            this.cashId = cashId;
            this.bankId = bankId;
            this.qrId = qrId;
            this.cardId = cardId;
        }

        public long getCashId() { return cashId; }
        public long getBankId() { return bankId; }
        public long getQrId() { return qrId; }
        public long getCardId() { return cardId; }
    }
}

