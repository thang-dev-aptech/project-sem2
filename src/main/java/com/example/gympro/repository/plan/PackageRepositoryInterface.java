package com.example.gympro.repository.plan;

import com.example.gympro.domain.plan.Plan;

import java.util.List;
import java.util.Optional;

public interface PackageRepositoryInterface {
    List<Plan> findAll(String searchTerm, Boolean isActive);
    List<Plan> findAll();
    Optional<Plan> findById(long id);
    Optional<Plan> insert(Plan plan);
    boolean update(Plan plan);
    boolean delete(long id);
}

