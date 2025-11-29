package com.example.gympro.service.settings;

import com.example.gympro.repository.settings.SettingsRepository;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service for Settings management
 */
public class SettingsService {
    private final SettingsRepository repository;

    public SettingsService() {
        this.repository = new SettingsRepository();
    }

    /**
     * Result class for prefix update operations
     */
    public static class PrefixUpdateResult {
        private final boolean success;
        private final int membersUpdated;
        private final int invoicesUpdated;
        private final String message;

        public PrefixUpdateResult(boolean success, int membersUpdated, int invoicesUpdated, String message) {
            this.success = success;
            this.membersUpdated = membersUpdated;
            this.invoicesUpdated = invoicesUpdated;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public int getMembersUpdated() { return membersUpdated; }
        public int getInvoicesUpdated() { return invoicesUpdated; }
        public String getMessage() { return message; }
    }

    // ========== Branch Info ==========
    public SettingsRepository.BranchInfo getBranchInfo() {
        return repository.getBranchInfo();
    }

    public boolean updateBranchInfo(String name, String address, String phone) {
        return repository.updateBranchInfo(name, address, phone);
    }

    // ========== Business Rules ==========
    // Grace Days is now hardcoded to 5 days (no longer configurable)
    public static final int DEFAULT_GRACE_DAYS = 5;
    
    public int getGraceDays() {
        return DEFAULT_GRACE_DAYS;
    }

    public String getMemberCodePrefix() {
        String value = repository.getStringSetting("MEMBER_CODE_PREFIX");
        return value != null ? value : "GYM";
    }

    public boolean setMemberCodePrefix(String prefix) {
        String oldPrefix = getMemberCodePrefix();
        boolean saved = repository.saveSetting("MEMBER_CODE_PREFIX", prefix, null);
        
        // If prefix changed, update all existing member codes
        if (saved && oldPrefix != null && !oldPrefix.equals(prefix)) {
            int updated = repository.updateAllMemberCodes(oldPrefix, prefix);
            if (updated < 0) {
                System.err.println("Warning: Failed to update existing member codes");
                // Still return true because setting was saved, just migration failed
            }
        }
        
        return saved;
    }

    /**
     * Set member code prefix and return detailed result including update count
     */
    public PrefixUpdateResult setMemberCodePrefixWithResult(String prefix) {
        String oldPrefix = getMemberCodePrefix();
        boolean saved = repository.saveSetting("MEMBER_CODE_PREFIX", prefix, null);
        
        int membersUpdated = 0;
        if (saved && oldPrefix != null && !oldPrefix.equals(prefix)) {
            membersUpdated = repository.updateAllMemberCodes(oldPrefix, prefix);
            if (membersUpdated < 0) {
                return new PrefixUpdateResult(false, 0, 0, 
                    "Failed to update existing member codes. Setting saved but migration failed.");
            }
        }
        
        String message = saved 
            ? (membersUpdated > 0 
                ? String.format("✅ Member code prefix updated! %d member codes changed from '%s' to '%s'", 
                    membersUpdated, oldPrefix, prefix)
                : "✅ Member code prefix saved successfully!")
            : "❌ Failed to save member code prefix";
        
        return new PrefixUpdateResult(saved, membersUpdated, 0, message);
    }

    public String getInvoicePrefix() {
        String value = repository.getStringSetting("INVOICE_PREFIX");
        return value != null ? value : "INV";
    }

    public boolean setInvoicePrefix(String prefix) {
        String oldPrefix = getInvoicePrefix();
        boolean saved = repository.saveSetting("INVOICE_PREFIX", prefix, null);
        
        // If prefix changed, update all existing invoice codes
        if (saved && oldPrefix != null && !oldPrefix.equals(prefix)) {
            int updated = repository.updateAllInvoiceCodes(oldPrefix, prefix);
            if (updated < 0) {
                System.err.println("Warning: Failed to update existing invoice codes");
                // Still return true because setting was saved, just migration failed
            }
        }
        
        return saved;
    }

    /**
     * Set invoice prefix and return detailed result including update count
     */
    public PrefixUpdateResult setInvoicePrefixWithResult(String prefix) {
        String oldPrefix = getInvoicePrefix();
        boolean saved = repository.saveSetting("INVOICE_PREFIX", prefix, null);
        
        int invoicesUpdated = 0;
        if (saved && oldPrefix != null && !oldPrefix.equals(prefix)) {
            invoicesUpdated = repository.updateAllInvoiceCodes(oldPrefix, prefix);
            if (invoicesUpdated < 0) {
                return new PrefixUpdateResult(false, 0, 0, 
                    "Failed to update existing invoice codes. Setting saved but migration failed.");
            }
        }
        
        String message = saved 
            ? (invoicesUpdated > 0 
                ? String.format("✅ Invoice prefix updated! %d invoice codes changed from '%s' to '%s'", 
                    invoicesUpdated, oldPrefix, prefix)
                : "✅ Invoice prefix saved successfully!")
            : "❌ Failed to save invoice prefix";
        
        return new PrefixUpdateResult(saved, 0, invoicesUpdated, message);
    }

    /**
     * Set both member and invoice prefixes and return combined result
     */
    public PrefixUpdateResult setPrefixesWithResult(String memberPrefix, String invoicePrefix) {
        PrefixUpdateResult memberResult = setMemberCodePrefixWithResult(memberPrefix);
        PrefixUpdateResult invoiceResult = setInvoicePrefixWithResult(invoicePrefix);
        
        boolean success = memberResult.isSuccess() && invoiceResult.isSuccess();
        int totalMembers = memberResult.getMembersUpdated();
        int totalInvoices = invoiceResult.getInvoicesUpdated();
        
        StringBuilder message = new StringBuilder();
        if (success) {
            message.append("✅ Business configuration saved successfully!");
            if (totalMembers > 0 || totalInvoices > 0) {
                message.append("\n\n");
                if (totalMembers > 0) {
                    message.append(String.format("📝 Updated %d member code(s)\n", totalMembers));
                }
                if (totalInvoices > 0) {
                    message.append(String.format("📄 Updated %d invoice code(s)", totalInvoices));
                }
            }
        } else {
            message.append("❌ Error saving business configuration!");
            if (!memberResult.isSuccess()) {
                message.append("\n").append(memberResult.getMessage());
            }
            if (!invoiceResult.isSuccess()) {
                message.append("\n").append(invoiceResult.getMessage());
            }
        }
        
        return new PrefixUpdateResult(success, totalMembers, totalInvoices, message.toString());
    }

    // Currency Symbol is now hardcoded to "VNĐ" (no longer configurable)
    public static final String DEFAULT_CURRENCY = "VNĐ";
    
    public String getCurrencySymbol() {
        return DEFAULT_CURRENCY;
    }
    
    // ========== New Business Settings ==========
    public int getExpiringReminderDays() {
        BigDecimal value = repository.getNumericSetting("EXPIRING_REMINDER_DAYS");
        return value != null ? value.intValue() : 7;
    }

    public boolean setExpiringReminderDays(int days) {
        return repository.saveSetting("EXPIRING_REMINDER_DAYS", null, BigDecimal.valueOf(days));
    }

    public double getTaxRate() {
        BigDecimal value = repository.getNumericSetting("TAX_RATE");
        return value != null ? value.doubleValue() : 0.0;
    }

    public boolean setTaxRate(double rate) {
        return repository.saveSetting("TAX_RATE", null, BigDecimal.valueOf(rate));
    }

    public boolean isAutoBackupEnabled() {
        BigDecimal value = repository.getNumericSetting("AUTO_BACKUP_ENABLED");
        return value != null && value.intValue() == 1;
    }

    public boolean setAutoBackupEnabled(boolean enabled) {
        return repository.saveSetting("AUTO_BACKUP_ENABLED", null, BigDecimal.valueOf(enabled ? 1 : 0));
    }

    public int getBackupRetentionDays() {
        BigDecimal value = repository.getNumericSetting("BACKUP_RETENTION_DAYS");
        return value != null ? value.intValue() : 30;
    }

    public boolean setBackupRetentionDays(int days) {
        return repository.saveSetting("BACKUP_RETENTION_DAYS", null, BigDecimal.valueOf(days));
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

