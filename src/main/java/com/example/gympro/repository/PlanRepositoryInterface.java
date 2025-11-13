package com.example.gympro.repository;

import com.example.gympro.viewModel.Plan;
import java.util.List;

public interface PlanRepositoryInterface {
    List<Plan> findAllActive();
}