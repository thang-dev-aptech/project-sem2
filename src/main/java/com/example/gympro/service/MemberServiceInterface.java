package com.example.gympro.service;

import com.example.gympro.viewModel.Member;
import java.util.List;
import java.util.Optional;

public interface MemberServiceInterface {
    List<Member> getFilteredMembers(String searchTerm, String statusFilter);
    List<Member> getAllMembers();
    Optional<Member> saveMember(Member member);
    boolean deleteMember(long id);
}
