package com.pawar.inventory.app.dto;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class UserDto {

    private Long userId;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date createdAt;
    private Date updatedAt;
    private Boolean loggedIn;
    private Boolean isActive;
    private Set<RoleDto> roles = new HashSet<>();

    public UserDto() {
    }

    public UserDto(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UserDto(String username, String email, String passwordHash, Boolean loggedIn) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.loggedIn = loggedIn;
    }

    public UserDto(Long userId, String username, String email, String passwordHash, Boolean loggedIn,
            Set<RoleDto> roles) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.loggedIn = loggedIn;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public UserDto(Long userId, String username, String email, String passwordHash, String firstName,
            String middleName, String lastName, Date createdAt, Date updatedAt,
            Boolean loggedIn, Set<RoleDto> roles, Boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.loggedIn = loggedIn;
        this.roles = roles != null ? roles : new HashSet<>();
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }

    @Override
    public String toString() {
        return "UserDto [userId=" + userId + ", username=" + username + ", email=" + email + ", passwordHash="
                + passwordHash + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", loggedIn=" + loggedIn + ", isActive=" + isActive
                + ", roles=" + roles + "]";
    }
}
