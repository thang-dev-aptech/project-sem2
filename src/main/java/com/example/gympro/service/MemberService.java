package com.example.gympro.service;

import com.example.gympro.mapper.member.MemberMapper;
import com.example.gympro.repository.member.MemberRepository;
import com.example.gympro.repository.member.MemberRepositoryInterface;
import com.example.gympro.viewModel.Member;

import java.util.List;
import java.util.Optional;

public class MemberService implements MemberServiceInterface {

    private final MemberRepositoryInterface memberRepository = new MemberRepository();

    @Override
    public List<Member> getAllMembers() {
        return getFilteredMembers(null, "Tất cả");
    }

    @Override
    public List<Member> getFilteredMembers(String searchTerm, String statusFilter) {
        String status = "Tất cả";
        if (statusFilter != null && !"Tất cả".equals(statusFilter)) {
            if (statusFilter.contains("(")) {
                status = statusFilter.substring(statusFilter.indexOf("(") + 1, statusFilter.indexOf(")")).toUpperCase();
            } else {
                status = statusFilter.toUpperCase();
            }
        }

        var domainMembers = memberRepository.findAll(searchTerm, status);
        return MemberMapper.toViewModelList(domainMembers);
    }

    @Override
    public Optional<Member> saveMember(Member member) {
        var domainMember = MemberMapper.toDomain(member);
        if (domainMember.getId() == 0) {
            return memberRepository.insert(domainMember).map(MemberMapper::toViewModel);
        } else {
            boolean updated = memberRepository.update(domainMember);
            return updated ? Optional.of(MemberMapper.toViewModel(domainMember)) : Optional.empty();
        }
    }

    @Override
    public boolean deleteMember(long id) {
        return memberRepository.delete(id);
    }
}
