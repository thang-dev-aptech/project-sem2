package com.example.gympro.service;

import com.example.gympro.domain.Role;
import com.example.gympro.domain.User;
import com.example.gympro.mapper.user.UserMapper;
import com.example.gympro.repository.RoleRepository;
import com.example.gympro.repository.UserRepository;
import com.example.gympro.repository.UserRoleRepository;
import com.example.gympro.viewModel.UserViewModel;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

/**
 * UserService - Business logic cho User Management
 */
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserService() {
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
        this.userRoleRepository = new UserRoleRepository();
    }

    public List<UserViewModel> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toViewModelList(users);
    }

    public Optional<UserViewModel> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toViewModel);
    }

    public Optional<UserViewModel> saveUser(UserViewModel vm, List<Long> roleIds) {
        User user = UserMapper.toDomain(vm);

        // Hash password nếu có
        if (vm.getPassword() != null && !vm.getPassword().trim().isEmpty()) {
            // Validate password min length từ settings
            com.example.gympro.service.settings.SettingsService settingsService = new com.example.gympro.service.settings.SettingsService();
            int minLength = settingsService.getPasswordMinLength();
            if (vm.getPassword().length() < minLength) {
                return Optional.empty(); // Password quá ngắn
            }
            String hashedPassword = BCrypt.hashpw(vm.getPassword(), BCrypt.gensalt());
            user.setPasswordHash(hashedPassword);
        } else if (user.getId() == null || user.getId() == 0) {
            // Tạo mới phải có password
            return Optional.empty();
        }

        Optional<User> savedUser;
        if (user.getId() == null || user.getId() == 0) {
            // Tạo mới
            savedUser = userRepository.insert(user);
        } else {
            // Cập nhật
            boolean updated = userRepository.update(user);
            savedUser = updated ? Optional.of(user) : Optional.empty();

            // Cập nhật password nếu có
            if (updated && vm.getPassword() != null && !vm.getPassword().trim().isEmpty()) {
                // Validate password min length từ settings
                com.example.gympro.service.settings.SettingsService settingsService = new com.example.gympro.service.settings.SettingsService();
                int minLength = settingsService.getPasswordMinLength();
                if (vm.getPassword().length() < minLength) {
                    return Optional.empty(); // Password quá ngắn
                }
                String hashedPassword = BCrypt.hashpw(vm.getPassword(), BCrypt.gensalt());
                userRepository.updatePassword(user.getId(), hashedPassword);
            }
        }

        if (savedUser.isPresent() && roleIds != null) {
            // Assign roles
            userRoleRepository.assignRoles(savedUser.get().getId(), roleIds);
            return Optional.of(UserMapper.toViewModel(savedUser.get()));
        }

        return savedUser.map(UserMapper::toViewModel);
    }

    public boolean deleteUser(Long id) {
        // Don't allow deleting yourself
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(id)) {
            return false;
        }
        return userRepository.delete(id);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> getUserRoles(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }
}

