package com.example.gympro.domain.member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Member {
    private final long id;
    private final long branchId;
    private final String memberCode;
    private final String fullName;
    private final String phone;
    private final String email;
    private final String gender;
    private final LocalDate dob;
    private final String address;
    private final MemberStatus status;
    private final String note;
    private final boolean deleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Member(Builder builder) {
        this.id = builder.id;
        this.branchId = builder.branchId;
        this.memberCode = builder.memberCode;
        this.fullName = builder.fullName;
        this.phone = builder.phone;
        this.email = builder.email;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.address = builder.address;
        this.status = builder.status;
        this.note = builder.note;
        this.deleted = builder.deleted;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public long getId() { return id; }
    public long getBranchId() { return branchId; }
    public String getMemberCode() { return memberCode; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public LocalDate getDob() { return dob; }
    public String getAddress() { return address; }
    public MemberStatus getStatus() { return status; }
    public String getNote() { return note; }
    public boolean isDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Builder toBuilder() {
        return new Builder()
                .id(id)
                .branchId(branchId)
                .memberCode(memberCode)
                .fullName(fullName)
                .phone(phone)
                .email(email)
                .gender(gender)
                .dob(dob)
                .address(address)
                .status(status)
                .note(note)
                .deleted(deleted)
                .createdAt(createdAt)
                .updatedAt(updatedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id && Objects.equals(memberCode, member.memberCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberCode);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", memberCode='" + memberCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", status=" + status +
                '}';
    }

    public static class Builder {
        private long id;
        private long branchId = 1;
        private String memberCode;
        private String fullName;
        private String phone;
        private String email;
        private String gender;
        private LocalDate dob;
        private String address;
        private MemberStatus status = MemberStatus.PENDING;
        private String note;
        private boolean deleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(long id) { this.id = id; return this; }
        public Builder branchId(long branchId) { this.branchId = branchId; return this; }
        public Builder memberCode(String memberCode) { this.memberCode = memberCode; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder gender(String gender) { this.gender = gender; return this; }
        public Builder dob(LocalDate dob) { this.dob = dob; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder status(MemberStatus status) { this.status = status; return this; }
        public Builder note(String note) { this.note = note; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Member build() {
            return new Member(this);
        }
    }
}

