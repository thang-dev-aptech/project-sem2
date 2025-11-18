package com.example.gympro.service;

import com.example.gympro.mapper.plan.PlanMapper;
import com.example.gympro.repository.plan.PackageRepository;
import com.example.gympro.repository.plan.PackageRepositoryInterface;
import com.example.gympro.viewModel.Package;

import java.util.List;
import java.util.Optional;

public class PackageService implements PackageServiceInterface {

    private final PackageRepositoryInterface packageRepositoryInterface = new PackageRepository();

    @Override
    public List<Package> getAllPackages() {
        return getFilteredPackages(null, "Tất cả");
    }

    @Override
    public List<Package> getFilteredPackages(String searchTerm, String statusFilter) {

        Boolean isActive = null;

        if ("Hiển thị (Active)".equals(statusFilter)) {
            isActive = true;
        } else if ("Ẩn (Inactive)".equals(statusFilter)) {
            isActive = false;
        }

        var plans = packageRepositoryInterface.findAll(searchTerm, isActive);
        return PlanMapper.toPackageViewList(plans);
    }

    @Override
    public Optional<Package> savePackage(Package pkg) {

        if (pkg.getName() == null || pkg.getName().trim().isEmpty() || pkg.getPrice().doubleValue() < 0) {
            System.err.println("Validation Error: Dữ liệu gói tập không hợp lệ.");
            return Optional.empty();
        }

        var domain = PlanMapper.toDomain(pkg);
        if (pkg.getId() == 0) {
            return packageRepositoryInterface.insert(domain).map(PlanMapper::toPackageView);
        } else {
            boolean updated = packageRepositoryInterface.update(domain);
            return updated ? Optional.of(PlanMapper.toPackageView(domain)) : Optional.empty();
        }
    }

    @Override
    public boolean deletePackage(long id) {
        return packageRepositoryInterface.delete(id);
    }
}
