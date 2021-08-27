package com.revature.portal.web.dtos;

import io.jsonwebtoken.Claims;
import com.revature.portal.datasource.models.User;

import java.util.Objects;

public class Principal {

    private String id;
    private String username;
    private String role;

    public Principal() {
        super();
    }

    public Principal(User subject) {
        this.id = subject.getId();
        this.username = subject.getUsername();
        this.role = subject.getRole();
    }

    public Principal(UserDTO subject) {
        this.id = subject.getId();
        this.username = subject.getUsername();
        this.role = subject.getRole();
    }

    public Principal(Claims jwtClaims) {
        this.id = jwtClaims.getId();
        this.username = jwtClaims.getSubject();
        this.role = jwtClaims.get("role", String.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Principal principal = (Principal) o;
        return Objects.equals(id, principal.id) && Objects.equals(username, principal.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "Principal{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

}
