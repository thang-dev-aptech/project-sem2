package com.example.gympro.service;

import java.util.stream.Collectors;

import com.example.gympro.repository.ExpiringMemberRepository;
import com.example.gympro.service.settings.SettingsService;
import com.example.gympro.viewModel.ExpiringMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExpiringMemberService {
    private final ExpiringMemberRepository repository = new ExpiringMemberRepository();
    private final SettingsService settingsService = new SettingsService();

    public ObservableList<ExpiringMember> getExpiringMembers(int daysLeft) {
        // Nếu daysLeft = 0 hoặc không chỉ định, lấy từ settings
        int reminderDays = (daysLeft > 0) ? daysLeft : settingsService.getReminderDays();
        ObservableList<ExpiringMember> list = repository.getExpiringMembers(reminderDays);

        list.forEach(m -> {
            if (m.getDaysLeft() < 0) {
                m.setStatus("❌ Hết hạn");
            } else if (m.getDaysLeft() == 0) {
                m.setStatus("🔴 Hết hạn hôm nay");
            } else if (m.getDaysLeft() <= 3) {
                m.setStatus("⏰ Sắp hết hạn");
            } else if (m.getDaysLeft() <= 7) {
                m.setStatus("⚠️ Cảnh báo (≤ 7 ngày)");
            } else {
                m.setStatus("📋 Cần theo dõi (≤ 14 ngày)");
            }
        });

        return list;
    }

    public ObservableList<ExpiringMember> search(ObservableList<ExpiringMember> list, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return list;
        }
        return FXCollections.observableArrayList(
                list.stream()
                        .filter(m -> m.getName().toLowerCase().contains(keyword.toLowerCase())
                                || m.getPhone().contains(keyword)
                                || m.getId().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toList())

        );
    }
}
