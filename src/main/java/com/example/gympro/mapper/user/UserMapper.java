package com.example.gympro.mapper.user;

import com.example.gympro.domain.Role;
import com.example.gympro.domain.User;
import com.example.gympro.repository.RoleRepository;
import com.example.gympro.viewModel.UserViewModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper - Chuyển đổi giữa domain User và ViewModel UserViewModel
 */
public class UserMapper {

    private static final RoleRepository roleRepository = new RoleRepository();

    public static UserViewModel toViewModel(User user) {
        if (user == null) return null;

        UserViewModel vm = new UserViewModel();
        vm.setId(user.getId());
        vm.setBranchId(user.getBranchId());
        vm.setUsername(user.getUsername());
        vm.setFullName(user.getFullName());
        vm.setEmail(user.getEmail());
        vm.setPhone(user.getPhone());
        vm.setIsActive(user.getIsActive() != null ? user.getIsActive() : true);
        vm.setLastLoginAt(user.getLastLoginAt());
        vm.setCreatedAt(user.getCreatedAt());
        vm.setUpdatedAt(user.getUpdatedAt());

        // Lấy roles và hiển thị
        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        String rolesDisplay = roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
        vm.setRolesDisplay(rolesDisplay.isEmpty() ? "Chưa có quyền" : rolesDisplay);

        return vm;
    }

    public static List<UserViewModel> toViewModelList(List<User> users) {
        return users.stream()
                .map(UserMapper::toViewModel)
                .collect(Collectors.toList());
    }

    public static User toDomain(UserViewModel vm) {
        if (vm == null) return null;

        User user = new User();
        if (vm.getId() > 0) {
            user.setId(vm.getId());
        }
        user.setBranchId(vm.getBranchId());
        user.setUsername(vm.getUsername());
        user.setFullName(vm.getFullName());
        user.setEmail(vm.getEmail());
        user.setPhone(vm.getPhone());
        user.setIsActive(vm.isActive());

        // Password hash sẽ được set riêng trong service

        return user;
    }
}

