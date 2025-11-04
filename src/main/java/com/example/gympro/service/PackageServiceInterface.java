package com.example.gympro.service;

import com.example.gympro.viewModel.Package;

import java.util.List;
import java.util.Optional;

public interface PackageServiceInterface {

    List<Package> getFilteredPackages(String searchTerm, String statusFilter);
    List<Package> getAllPackages();
    Optional<Package> savePackage(Package pkg);
    boolean deletePackage(long id);
}