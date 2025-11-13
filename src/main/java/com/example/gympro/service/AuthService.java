package com.example.gympro.service;

import com.example.gympro.domain.Role;
import com.example.gympro.domain.User;
import com.example.gympro.repository.RoleRepository;
import com.example.gympro.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final User user;
        public final List<Role> roles;

        public AuthResult(boolean success, String message, User user, List<Role> roles) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.roles = roles;
        }
    }

    public AuthService() {
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
    }

    public AuthResult authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return new AuthResult(false, "Vui lòng nhập username và password", null, null);
        }

        User user = userRepository.findByUsername(username.trim());
        if (user == null) {
            return new AuthResult(false, "Tài khoản không tồn tại", null, null);
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            return new AuthResult(false, "Tài khoản đã bị vô hiệu hóa", null, null);
        }

        String hash = user.getPasswordHash();
        if (hash == null || hash.isBlank()) {
            return new AuthResult(false, "Tài khoản không có mật khẩu", null, null);
        }
        
        boolean passwordOk = false;
        try {
            // Trim hash để tránh khoảng trắng từ DB
            hash = hash.trim();
            passwordOk = BCrypt.checkpw(rawPassword, hash);
        } catch (IllegalArgumentException e) {
            System.err.println("BCrypt verification error for user " + username + ": " + e.getMessage());
            System.err.println("Hash length: " + (hash != null ? hash.length() : 0));
            passwordOk = false;
        } catch (Exception e) {
            System.err.println("Unexpected error during password verification: " + e.getMessage());
            e.printStackTrace();
            passwordOk = false;
        }
        
        if (!passwordOk) {
            return new AuthResult(false, "Mật khẩu không đúng", null, null);
        }

        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        return new AuthResult(true, "Đăng nhập thành công", user, roles);
    }
}


