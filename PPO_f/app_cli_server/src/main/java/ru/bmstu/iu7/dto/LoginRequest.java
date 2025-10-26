package ru.bmstu.iu7.dto;

public class LoginRequest {
    private String name;
    private String password;

    // геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
