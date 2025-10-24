package ru.bmstu.iu7.API.dto;

public class PutUserRequest {
    private String username;
    private String password;
    private int age;
    private boolean gender;
    private String role;

    // геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isGender() { return gender; }
    public void setGender(boolean gender) { this.gender = gender; }
}
