package com.example.gympro.repository.subscription;

import com.example.gympro.domain.member.Member;
import com.example.gympro.domain.plan.Plan;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public interface RegistrationRepositoryInterface {
    long createSubscription(Connection conn, Member member, Plan plan, LocalDate startDate, LocalDate endDate,
                            long createdByUserId) throws SQLException;

    long createInvoice(Connection conn, Member member, Plan plan, long subscriptionId, long createdByUserId)
            throws SQLException;

    boolean createInvoiceItem(Connection conn, Plan plan, long invoiceId) throws SQLException;
}

