package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.API.model.AUser;

public class AUserBuilder {
    private Long id = 0L;
    private String name = "lena";
    private int age = 45;
    private boolean active = true;
    private String password = "1234";
    private String role = "user";

    public static AUserBuilder defaultAUser() {
        return new AUserBuilder();
    }

    public AUserBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AUser build() {
        return new AUser(id, name, age, active, password, role);
    }
}
