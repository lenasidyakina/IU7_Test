package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.impl.model.User;

public class UserBuilder {
    private Long id = 0L;
    private String name = "lena";
    private int age = 45;
    private boolean active = true;
    private String password = "1234";
    private String role = "user";

    public static UserBuilder defaultUser() {
        return new UserBuilder();
    }

    public UserBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public User build() {
        return new User(id, name, age, active, password, role);
    }
}
