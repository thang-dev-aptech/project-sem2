package com.example.gympro.repository;

import com.example.gympro.viewModel.Member;
import com.example.gympro.viewModel.Plan;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public interface RegistrationRepositoryInterface {
    long createSubscription(Connection conn, Member member, Plan plan, LocalDate startDate, LocalDate endDate, long userId) throws SQLException;
    long createInvoice(Connection conn, Member member, Plan plan, long subscriptionId, long userId) throws SQLException;
    boolean createInvoiceItem(Connection conn, Plan plan, long invoiceId) throws SQLException;
}