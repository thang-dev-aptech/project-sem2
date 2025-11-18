package com.example.gympro.mapper.billing;

import com.example.gympro.domain.billing.Invoice;
import com.example.gympro.mapper.member.MemberMapper;
import com.example.gympro.mapper.plan.PlanMapper;
import com.example.gympro.viewModel.Member;
import com.example.gympro.viewModel.Plan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public final class InvoiceMapper {
    private InvoiceMapper() {}

    public static com.example.gympro.viewModel.Invoice toViewModel(Invoice domain) {
        if (domain == null) return null;
        Member memberVm = MemberMapper.toViewModel(domain.getMember());
        Plan planVm = PlanMapper.toPlanView(domain.getPlan());
        return new com.example.gympro.viewModel.Invoice(
                domain.getId(),
                domain.getInvoiceNo(),
                domain.getTotalAmount().doubleValue(),
                domain.getIssueDate(),
                domain.getSubscriptionId() == null ? 0 : domain.getSubscriptionId(),
                memberVm,
                planVm
        );
    }

    public static List<com.example.gympro.viewModel.Invoice> toViewModels(List<Invoice> invoices) {
        return invoices.stream()
                .map(InvoiceMapper::toViewModel)
                .collect(Collectors.toList());
    }

    public static Invoice toDomain(com.example.gympro.viewModel.Invoice vm) {
        if (vm == null) return null;
        var memberDomain = MemberMapper.toDomain(vm.getMember());
        var planDomain = PlanMapper.toDomain(vm.getPlan());
        Long subscriptionId = vm.getSubscriptionId() == 0 ? null : vm.getSubscriptionId();

        return new Invoice.Builder()
                .id(vm.getInvoiceId())
                .invoiceNo(vm.getInvoiceNo())
                .subscriptionId(subscriptionId)
                .issueDate(vm.getIssueDate() != null ? vm.getIssueDate() : LocalDate.now())
                .totalAmount(BigDecimal.valueOf(vm.getTotalAmount()))
                .member(memberDomain)
                .plan(planDomain)
                .build();
    }
}

