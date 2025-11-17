package com.example.gympro.service;

import com.example.gympro.viewModel.ExpiringMember;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationService {

    public int sendBulkReminder(List<ExpiringMember> members) {
        if (members == null || members.isEmpty()) {
            System.out.println("⚠ Không có thành viên nào để gửi nhắc.");
            return 0;
        }

        int count = 0;
        for (ExpiringMember member : members) {
            if (sendReminder(member)) {
                count++;
            }
        }

        System.out.println("📩 Đã gửi nhắc cho " + count + "/" + members.size() + " thành viên");
        return count;
    }

    /** Gửi chung (logic mock: email hoặc sms đều dùng chung message) */
    public boolean sendReminder(ExpiringMember member) {
        System.out.println("📨 Gửi nhắc nhở đến " + member.getName()
                + " (" + member.getPhone() + ") → " + buildReminderMessage(member));
        return true; // giả sử luôn thành công
    }

    public boolean sendEmailReminder(ExpiringMember member) {
        System.out.println("📧 EMAIL → " + member.getName() + ": " + buildReminderMessage(member));
        return true;
    }

    public boolean sendSMSReminder(ExpiringMember member) {
        System.out.println("📱 SMS → " + member.getName() + ": " + buildReminderMessage(member));
        return true;
    }

    private String buildReminderMessage(ExpiringMember member) {
        LocalDate exp;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            exp = (member.getExpiry() != null)
                    ? LocalDate.parse(member.getExpiry(), formatter)
                    : LocalDate.now();
        } catch (Exception e) {
            exp = LocalDate.now();
        }

        return "Xin chào " + member.getName()
                + ", gói tập gym của bạn sẽ hết hạn vào " + exp
                + ". Vui lòng gia hạn để không bị gián đoạn.";
    }
}
