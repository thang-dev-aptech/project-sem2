package com.example.gympro.mapper.plan;

import com.example.gympro.viewModel.Package;
import com.example.gympro.viewModel.Plan;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public final class PlanMapper {
    private PlanMapper() {}

    public static Package toPackageView(com.example.gympro.domain.plan.Plan plan) {
        if (plan == null) return null;
        Package vm = new Package();
        vm.setId(plan.getId());
        vm.setBranchId(plan.getBranchId());
        vm.setCode(plan.getCode());
        vm.setName(plan.getName());
        vm.setDescription(plan.getDescription());
        vm.setPrice(plan.getPrice());
        vm.setDurationDays(plan.getDurationDays());
        vm.setIsActive(plan.isActive());
        vm.setCreatedAt(plan.getCreatedAt());
        vm.setUpdatedAt(plan.getUpdatedAt());
        return vm;
    }

    public static Plan toPlanView(com.example.gympro.domain.plan.Plan plan) {
        if (plan == null) return null;
        return new Plan(
                plan.getId(),
                plan.getName(),
                plan.getPrice().doubleValue(),
                plan.getDurationDays()
        );
    }

    public static com.example.gympro.domain.plan.Plan toDomain(Package vm) {
        if (vm == null) return null;
        BigDecimal price = vm.getPrice() != null ? vm.getPrice() : BigDecimal.ZERO;
        return new com.example.gympro.domain.plan.Plan.Builder()
                .id(vm.getId())
                .branchId(vm.getBranchId())
                .code(vm.getCode())
                .name(vm.getName())
                .description(vm.getDescription())
                .price(price)
                .durationDays(vm.getDurationDays())
                .active(vm.isActive())
                .createdAt(vm.getCreatedAt())
                .updatedAt(vm.getUpdatedAt())
                .build();
    }

    public static com.example.gympro.domain.plan.Plan toDomain(Plan vm) {
        if (vm == null) return null;
        return new com.example.gympro.domain.plan.Plan.Builder()
                .id(vm.getId())
                .name(vm.getName())
                .price(BigDecimal.valueOf(vm.getPrice()))
                .durationDays(vm.getDurationDays())
                .build();
    }

    public static List<Package> toPackageViewList(List<com.example.gympro.domain.plan.Plan> plans) {
        return plans.stream().map(PlanMapper::toPackageView).collect(Collectors.toList());
    }

    public static List<Plan> toPlanViewList(List<com.example.gympro.domain.plan.Plan> plans) {
        return plans.stream().map(PlanMapper::toPlanView).collect(Collectors.toList());
    }
}

