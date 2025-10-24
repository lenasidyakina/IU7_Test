package ru.bmstu.iu7.impl.model;


import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public int age;
    public boolean gender;
    public String password;
    public String role;

    public User(){}
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public User(Long id, String name, int age, boolean gender, String password, String role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.role = role;
    }

    public User(String name, String password, int age, boolean gender, String role) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.role = role;
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
    public void setAge(int age) {
        this.age = age;
    }
    public boolean isGender() {
        return this.gender;
    }
    public void setGender(boolean gender) {
        this.gender = gender;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {return this.role;}

    @Override
    public boolean equals(Object obj) {
        boolean eq = id != null && obj != null &&
                id.equals(((User)obj).id) && name.equals(((User)obj).name) && age == ((User)obj).age
                && gender == ((User)obj).gender && password.equals(((User)obj).password)
                && role.equals(((User)obj).role);
        return eq;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
