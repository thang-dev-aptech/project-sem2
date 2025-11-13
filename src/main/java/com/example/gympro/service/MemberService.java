package com.example.gympro.service;

import com.example.gympro.repository.MemberRepositoryInterface;
import com.example.gympro.repository.MemberRepository;
import com.example.gympro.viewModel.Member;
import java.util.List;
import java.util.Optional;

public class MemberService implements MemberServiceInterface {

    private final MemberRepository memberRepository = new MemberRepository();

    @Override
    public List<Member> getAllMembers() {
        return getFilteredMembers(null, "Tất cả");
    }

    @Override
    public List<Member> getFilteredMembers(String searchTerm, String statusFilter) {
        // Trạng thái từ ComboBox (ví dụ: "Đang hoạt động (ACTIVE)")
        // cần được chuyển đổi sang giá trị ENUM (ví dụ: "ACTIVE")

        String status = "Tất cả"; // Mặc định
        if (statusFilter != null && !statusFilter.equals("Tất cả")) {
            // Trích xuất giá trị ENUM từ chuỗi (ví dụ: "Hoạt động (ACTIVE)" -> "ACTIVE")
            if (statusFilter.contains("(")) {
                status = statusFilter.substring(statusFilter.indexOf("(") + 1, statusFilter.indexOf(")")).toUpperCase();
            } else {
                status = statusFilter.toUpperCase();
            }
        }

        return memberRepository.findAll(searchTerm, status);
    }

    @Override
    public Optional<Member> saveMember(Member member) {
        // Logic nghiệp vụ (ví dụ: kiểm tra trùng SĐT, Email)
        // ...

        if (member.getId() == 0) {
            return memberRepository.insert(member);
        } else {
            return memberRepository.update(member) ? Optional.of(member) : Optional.empty();
        }
    }

    @Override
    public boolean deleteMember(long id) {
        // Sử dụng soft delete
        return memberRepository.delete(id);
    }
}
