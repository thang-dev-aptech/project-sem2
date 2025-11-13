package com.example.gympro.service;

import com.example.gympro.viewModel.Member;
import com.example.gympro.viewModel.Plan;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface (Hợp đồng) cho RegistrationService.
 * Controller sẽ chỉ làm việc với Interface này.
 */
public interface RegistrationServiceInterface {

    /**
     * Lấy danh sách hội viên cho ComboBox
     */
    List<Member> getMembersForRegistration();

    /**
     * Lấy danh sách gói tập cho ComboBox
     */
    List<Plan> getPlansForRegistration();

    /**
     * Xử lý logic nghiệp vụ: Tạo Subscription VÀ Invoice
     */
    boolean createRegistrationAndInvoice(
            Member member,
            Plan plan,
            LocalDate startDate,
            LocalDate endDate,
            long createdByUserId
    );
}