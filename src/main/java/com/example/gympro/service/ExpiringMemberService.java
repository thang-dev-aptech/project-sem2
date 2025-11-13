package com.example.gympro.service;

import java.util.stream.Collectors;

import com.example.gympro.repository.ExpiringMemberRepository;
import com.example.gympro.viewModel.ExpiringMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExpiringMemberService {
    private final ExpiringMemberRepository repository = new ExpiringMemberRepository();

    public ObservableList<ExpiringMember> getExpiringMembers(int daysLeft) {
        ObservableList<ExpiringMember> list = repository.getExpiringMembers(daysLeft);

        list.forEach(m -> {
            if (m.getDaysLeft() < 0) {
                m.setStatus("❌ Hết hạn");
            } else if (m.getDaysLeft() <= 3) {
                m.setStatus("⏰ Sắp hết hạn");
            } else {
                m.setStatus("✅ Còn hạn");
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
