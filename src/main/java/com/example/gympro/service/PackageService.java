package com.example.gympro.service;

import com.example.gympro.repository.PackageRepository;
import com.example.gympro.repository.PackageRepositoryInterface;
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

        // Chuyển đổi chuỗi trạng thái từ ComboBox thành Boolean cho Repository
        if ("Hiển thị (Active)".equals(statusFilter)) {
            isActive = true;
        } else if ("Ẩn (Inactive)".equals(statusFilter)) {
            isActive = false;
        }

        return packageRepositoryInterface.findAll(searchTerm, isActive);
    }

    @Override
    public Optional<Package> savePackage(Package pkg) {

        // --- LOGIC NGHIỆP VỤ / VALIDATION ---
        if (pkg.getName() == null || pkg.getName().trim().isEmpty() || pkg.getPrice().doubleValue() < 0) {
            System.err.println("Validation Error: Dữ liệu gói tập không hợp lệ.");
            return Optional.empty();
        }

        if (pkg.getId() == 0) {
            // INSERT
            return packageRepositoryInterface.insert(pkg);
        } else {
            // UPDATE
            return packageRepositoryInterface.update(pkg) ? Optional.of(pkg) : Optional.empty();
        }
    }

    @Override
    public boolean deletePackage(long id) {
        // Logic nghiệp vụ xóa
        return packageRepositoryInterface.delete(id);
    }
}