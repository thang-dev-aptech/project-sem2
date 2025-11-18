package com.example.gympro.mapper.member;

import com.example.gympro.domain.member.MemberStatus;
import com.example.gympro.viewModel.Member;

import java.util.List;
import java.util.stream.Collectors;

public final class MemberMapper {
    private MemberMapper() {}

    public static Member toViewModel(com.example.gympro.domain.member.Member domain) {
        if (domain == null) return null;

        Member vm = new Member();
        vm.setId(domain.getId());
        vm.setBranchId(domain.getBranchId());
        vm.setMemberCode(domain.getMemberCode());
        vm.setFullName(domain.getFullName());
        vm.setPhone(domain.getPhone());
        vm.setEmail(domain.getEmail());
        vm.setGender(domain.getGender());
        vm.setDob(domain.getDob());
        vm.setAddress(domain.getAddress());
        vm.setStatus(domain.getStatus().name());
        vm.setNote(domain.getNote());
        vm.setCreatedAt(domain.getCreatedAt());
        vm.setUpdatedAt(domain.getUpdatedAt());
        return vm;
    }

    public static List<Member> toViewModelList(List<com.example.gympro.domain.member.Member> domains) {
        return domains.stream()
                .map(MemberMapper::toViewModel)
                .collect(Collectors.toList());
    }

    public static com.example.gympro.domain.member.Member toDomain(Member vm) {
        if (vm == null) return null;

        return new com.example.gympro.domain.member.Member.Builder()
                .id(vm.getId())
                .branchId(vm.getBranchId())
                .memberCode(vm.getMemberCode())
                .fullName(vm.getFullName())
                .phone(vm.getPhone())
                .email(vm.getEmail())
                .gender(vm.getGender())
                .dob(vm.getDob())
                .address(vm.getAddress())
                .status(MemberStatus.fromDatabase(vm.getStatus()))
                .note(vm.getNote())
                .createdAt(vm.getCreatedAt())
                .updatedAt(vm.getUpdatedAt())
                .build();
    }
}

