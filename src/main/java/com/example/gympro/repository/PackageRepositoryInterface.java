package com.example.gympro.repository;

import com.example.gympro.viewModel.Package;

import java.util.List;
import java.util.Optional;

public interface PackageRepositoryInterface {

    // Hàm hỗ trợ tìm kiếm và lọc
    List<Package> findAll(String searchTerm, Boolean isActive);

    // Hàm không tham số (gọi hàm trên với null)
    List<Package> findAll();

    Optional<Package> findById(long id);
    Optional<Package> insert(Package pkg);
    boolean update(Package pkg);
    boolean delete(long id);
}