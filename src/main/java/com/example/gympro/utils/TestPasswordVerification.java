package com.example.gympro.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Test utility to verify password hashes match
 */
public class TestPasswordVerification {
    
    public static void main(String[] args) {
        System.out.println("Testing BCrypt password verification...\n");
        
        // Test cases from database
        String[][] testCases = {
            {"admin123", "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi", "admin"},
            {"staff123", "$2a$10$4thd6VL22evbKAYTGZQHDuIvlgpy3PaiL5dNQdXan1jfj2RgjAsjy", "staff"},
            {"manager123", "$2a$10$rORI/A9ufqDyL3QLN.XcLOT9n/SQzQPbT3r91RdyaeSWmwtULjquy", "manager"}
        };
        
        for (String[] test : testCases) {
            String password = test[0];
            String hash = test[1];
            String username = test[2];
            
            try {
                boolean matches = BCrypt.checkpw(password, hash);
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                System.out.println("Hash: " + hash);
                System.out.println("Verification: " + (matches ? "✓ MATCH" : "✗ NO MATCH"));
                System.out.println("---");
            } catch (Exception e) {
                System.out.println("Username: " + username);
                System.out.println("ERROR: " + e.getMessage());
                System.out.println("---");
            }
        }
        
        // Test generating new hash
        System.out.println("\nGenerating new hash for 'admin123':");
        String newHash = PasswordHasher.hashPassword("admin123");
        System.out.println("New hash: " + newHash);
        boolean newMatches = PasswordHasher.verifyPassword("admin123", newHash);
        System.out.println("New hash verification: " + (newMatches ? "✓ MATCH" : "✗ NO MATCH"));
    }
}

