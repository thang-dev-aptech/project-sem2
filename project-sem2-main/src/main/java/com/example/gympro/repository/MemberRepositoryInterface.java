package com.example.gympro.repository;

import com.example.gympro.viewModel.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepositoryInterface {
    List<Member> findAll(String searchTerm, String status);
    List<Member> findAll();
    Optional<Member> findById(long id);
    Optional<Member> insert(Member member);
    boolean update(Member member);
    boolean delete(long id);
}
