package com.hireme.dto;

import com.hireme.model.enums.Role;

public class UserResponse {

    private Long id;
    private String email;
    private Role role;
    private Long profileId;

    public UserResponse() {}

    public UserResponse(Long id, String email, Role role, Long profileId) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.profileId = profileId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
}
