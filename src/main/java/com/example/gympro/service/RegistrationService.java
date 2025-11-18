package com.example.gympro.service;

import com.example.gympro.mapper.member.MemberMapper;
import com.example.gympro.mapper.plan.PlanMapper;
import com.example.gympro.repository.member.MemberRepository;
import com.example.gympro.repository.member.MemberRepositoryInterface;
import com.example.gympro.repository.plan.PlanRepository;
import com.example.gympro.repository.plan.PlanRepositoryInterface;
import com.example.gympro.repository.subscription.RegistrationRepository;
import com.example.gympro.repository.subscription.RegistrationRepositoryInterface;
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
        return MemberMapper.toViewModelList(memberRepository.findAll(null, null));
    }

    @Override
    public List<Plan> getPlansForRegistration() {
        return PlanMapper.toPlanViewList(planRepository.findAllActive());
    }

    @Override
    public boolean createRegistrationAndInvoice(Member member, Plan plan, LocalDate startDate, LocalDate endDate,
            long createdByUserId) {
        Connection conn = null;
        var memberDomain = MemberMapper.toDomain(member);
        var planDomain = PlanMapper.toDomain(plan);

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            long subscriptionId = registrationRepository.createSubscription(conn, memberDomain, planDomain, startDate, endDate,
                    createdByUserId);
            long invoiceId = registrationRepository.createInvoice(conn, memberDomain, planDomain, subscriptionId, createdByUserId);
            boolean itemCreated = registrationRepository.createInvoiceItem(conn, planDomain, invoiceId);

            if (subscriptionId > 0 && invoiceId > 0 && itemCreated) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Member getMemberById(long id) {
        return memberRepository.findById(id)
                .map(MemberMapper::toViewModel)
                .orElse(null);
    }

    public Plan getPlanByName(String name) {
        return PlanMapper.toPlanViewList(planRepository.findAllActive())
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

}