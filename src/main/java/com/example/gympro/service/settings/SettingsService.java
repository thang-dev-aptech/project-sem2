package com.example.gympro.service.settings;

import com.example.gympro.repository.settings.SettingsRepository;

import java.math.BigDecimal;
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


    // ========== System Info ==========
    public Map<String, String> getAllSettings() {
        return repository.getAllSettings();
    }
}

