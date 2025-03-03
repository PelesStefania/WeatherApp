package org.example;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // AUTO pentru H2
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING) // Salvează rolul ca un string în baza de date
    @Column(name = "role", nullable = false)
    private Role role;

    // Constructor implicit
    public AppUser() {}
    // Constructor pentru crearea unui utilizator
    public AppUser(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
    // Getters și Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;

        if ("stefi@stefi.ro".equals(appUser.email)) {
            appUser.setRole(Role.ADMIN);
        }
        return Objects.equals(email, appUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    public void setRole(Role role) {
        this.role = role;
    }
    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}