package com.example.gympro.viewModel;

public class PieStats {
    private int activeMembers;
    private int expiringMembers;
    private int expiredMembers;

    public PieStats(int activeMembers, int expiringMembers, int expiredMembers) {
        this.activeMembers = activeMembers;
        this.expiredMembers = expiredMembers;
    }

    public int getActiveMembers() {
        return activeMembers;
    }

    public int getExpiringMembers() {
        return expiringMembers;
    }

    public int getExpiredMembers() {
        return expiredMembers;
    }
}
