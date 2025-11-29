package com.example.gympro.service;

import com.example.gympro.mapper.member.MemberMapper;
import com.example.gympro.repository.member.MemberRepository;
import com.example.gympro.repository.member.MemberRepositoryInterface;
import com.example.gympro.service.settings.SettingsService;
import com.example.gympro.viewModel.Member;

import java.util.List;
import java.util.Optional;

public class MemberService implements MemberServiceInterface {

    private final MemberRepositoryInterface memberRepository = new MemberRepository();
    private final SettingsService settingsService = new SettingsService();

    @Override
    public List<Member> getAllMembers() {
        return getFilteredMembers(null, "All");
    }

    @Override
    public List<Member> getFilteredMembers(String searchTerm, String statusFilter) {
        String status = "All";
        if (statusFilter != null && !"All".equalsIgnoreCase(statusFilter) && !"Tất cả".equals(statusFilter)) {
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
            // Auto-generate member code for new members
            if (member.getMemberCode() == null || member.getMemberCode().trim().isEmpty() || 
                member.getMemberCode().equals("(Auto-generated)")) {
                String prefix = settingsService.getMemberCodePrefix();
                String generatedCode = ((MemberRepository) memberRepository).generateNextMemberCode(prefix);
                domainMember = domainMember.toBuilder().memberCode(generatedCode).build();
            }
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

    /**
     * Sync member status with subscription expiration dates
     * Should be called periodically or when loading members
     */
    public void syncMemberStatus() {
        ((MemberRepository) memberRepository).syncMemberStatusWithSubscription();
    }
}
