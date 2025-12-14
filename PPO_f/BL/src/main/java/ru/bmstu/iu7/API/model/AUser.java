package ru.bmstu.iu7.API.model;

import java.util.Objects;

public class AUser {

    private Long id;
    private String name;
    private int age;
    private boolean gender;
    private String password;
    private String role;

    public AUser() { }

    public AUser(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public AUser(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public AUser(Long id, String name, int age, boolean gender, String password, String role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AUser)) return false;
        AUser other = (AUser) o;
        return Objects.equals(id, other.id) &&
                Objects.equals(name, other.name) &&
                age == other.age &&
                gender == other.gender &&
                Objects.equals(password, other.password) &&
                Objects.equals(role, other.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, gender, password, role);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
