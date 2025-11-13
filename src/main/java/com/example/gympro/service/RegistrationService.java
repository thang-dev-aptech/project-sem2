package com.example.gympro.service;

import com.example.gympro.repository.*;
import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.Member;
import com.example.gympro.viewModel.Plan;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RegistrationService implements RegistrationServiceInterface {

    private final MemberRepositoryInterface memberRepository = new MemberRepository();
    private final PlanRepositoryInterface planRepository = new PlanRepository();
    private final RegistrationRepositoryInterface registrationRepository = new RegistrationRepository();
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    @Override
    public List<Member> getMembersForRegistration() {
        return memberRepository.findAll(null, null);
    }

    @Override
    public List<Plan> getPlansForRegistration() {
        return planRepository.findAllActive();
    }

    @Override
    public boolean createRegistrationAndInvoice(Member member, Plan plan, LocalDate startDate, LocalDate endDate, long createdByUserId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            long subscriptionId = registrationRepository.createSubscription(conn, member, plan, startDate, endDate, createdByUserId);
            long invoiceId = registrationRepository.createInvoice(conn, member, plan, subscriptionId, createdByUserId);
            boolean itemCreated = registrationRepository.createInvoiceItem(conn, plan, invoiceId);

            if (subscriptionId > 0 && invoiceId > 0 && itemCreated) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}