package ru.bmstu.iu7.API.model;

import java.util.Objects;

public class AUser
{
    public Long id;
    public String name;
    public int age;
    public boolean gender;
    public String password;
    public String role;

    public AUser(){}
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
        this.name = name;
        this.password = password;
        this.id = id;
        this.age = age;
        this.gender = gender;
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
        return this.age;
    }
    public boolean isGender() {
        return this.gender;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return this.role;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
}
