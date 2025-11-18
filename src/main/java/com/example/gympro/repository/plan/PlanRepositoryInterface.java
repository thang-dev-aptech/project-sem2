package com.example.gympro.repository.plan;

import com.example.gympro.domain.plan.Plan;

import java.util.List;

public interface PlanRepositoryInterface {
    List<Plan> findAllActive();
}

