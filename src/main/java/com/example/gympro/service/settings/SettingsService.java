package com.example.gympro.service.settings;

import com.example.gympro.repository.settings.SettingsRepository;
import com.example.gympro.viewModel.EventDiscountViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service cho Settings
 */
public class SettingsService {
    private final SettingsRepository repository;

    public SettingsService() {
        this.repository = new SettingsRepository();
    }

    // ========== Branch Info ==========
    public SettingsRepository.BranchInfo getBranchInfo() {
        return repository.getBranchInfo();
    }

    public boolean updateBranchInfo(String name, String address, String phone) {
        return repository.updateBranchInfo(name, address, phone);
    }

    // ========== Business Rules ==========
    public int getGraceDays() {
        BigDecimal value = repository.getNumericSetting("GRACE_DAYS");
        return value != null ? value.intValue() : 5;
    }

    public boolean setGraceDays(int days) {
        return repository.saveSetting("GRACE_DAYS", null, BigDecimal.valueOf(days));
    }

    public int getReminderDays() {
        BigDecimal value = repository.getNumericSetting("REMINDER_DAYS");
        return value != null ? value.intValue() : 7;
    }

    public boolean setReminderDays(int days) {
        return repository.saveSetting("REMINDER_DAYS", null, BigDecimal.valueOf(days));
    }

    public String getMemberCodePrefix() {
        String value = repository.getStringSetting("MEMBER_CODE_PREFIX");
        return value != null ? value : "GYM";
    }

    public boolean setMemberCodePrefix(String prefix) {
        return repository.saveSetting("MEMBER_CODE_PREFIX", prefix, null);
    }

    public String getInvoicePrefix() {
        String value = repository.getStringSetting("INVOICE_PREFIX");
        return value != null ? value : "INV";
    }

    public boolean setInvoicePrefix(String prefix) {
        return repository.saveSetting("INVOICE_PREFIX", prefix, null);
    }

    public String getCurrencySymbol() {
        String value = repository.getStringSetting("CURRENCY_SYMBOL");
        return value != null ? value : "₫";
    }

    public boolean setCurrencySymbol(String symbol) {
        return repository.saveSetting("CURRENCY_SYMBOL", symbol, null);
    }

    // ========== Notifications ==========
    public boolean isAutoReminderEnabled() {
        String value = repository.getStringSetting("AUTO_REMINDER_ENABLED");
        return value != null && value.equalsIgnoreCase("true");
    }

    public boolean setAutoReminderEnabled(boolean enabled) {
        return repository.saveSetting("AUTO_REMINDER_ENABLED", String.valueOf(enabled), null);
    }

    // ========== Security ==========
    public int getPasswordMinLength() {
        BigDecimal value = repository.getNumericSetting("PASSWORD_MIN_LENGTH");
        return value != null ? value.intValue() : 6;
    }

    public boolean setPasswordMinLength(int length) {
        return repository.saveSetting("PASSWORD_MIN_LENGTH", null, BigDecimal.valueOf(length));
    }

    public int getSessionTimeout() {
        BigDecimal value = repository.getNumericSetting("SESSION_TIMEOUT");
        return value != null ? value.intValue() : 30;
    }

    public boolean setSessionTimeout(int minutes) {
        return repository.saveSetting("SESSION_TIMEOUT", null, BigDecimal.valueOf(minutes));
    }

    public int getMaxLoginAttempts() {
        BigDecimal value = repository.getNumericSetting("MAX_LOGIN_ATTEMPTS");
        return value != null ? value.intValue() : 5;
    }

    public boolean setMaxLoginAttempts(int attempts) {
        return repository.saveSetting("MAX_LOGIN_ATTEMPTS", null, BigDecimal.valueOf(attempts));
    }

    public int getLockoutDuration() {
        BigDecimal value = repository.getNumericSetting("LOCKOUT_DURATION");
        return value != null ? value.intValue() : 15;
    }

    public boolean setLockoutDuration(int minutes) {
        return repository.saveSetting("LOCKOUT_DURATION", null, BigDecimal.valueOf(minutes));
    }

    // ========== Event Discounts ==========
    public List<EventDiscountViewModel> getEventDiscounts() {
        // TODO: Implement get event discounts from JSON
        return List.of();
    }

    public boolean saveEventDiscounts(List<EventDiscountViewModel> discounts) {
        // TODO: Convert ViewModel to EventDiscount and save to JSON
        // Tạm thời: return true
        return true;
    }

    // ========== System Info ==========
    public Map<String, String> getAllSettings() {
        return repository.getAllSettings();
    }
}

