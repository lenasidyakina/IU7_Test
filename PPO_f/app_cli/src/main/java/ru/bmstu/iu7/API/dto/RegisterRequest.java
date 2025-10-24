package ru.bmstu.iu7.API.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private int age;
    private boolean gender;

    // геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public boolean isGender() { return gender; }
    public void setGender(boolean gender) { this.gender = gender; }
}
