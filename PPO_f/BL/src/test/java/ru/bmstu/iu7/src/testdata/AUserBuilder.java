package ru.bmstu.iu7.src.testdata;

import ru.bmstu.iu7.API.model.AUser;

public class AUserBuilder {
    private String name = "DefaultName";
    private String password = "DefaultPassword";
    private int age = 20;
    private boolean gender = true;

    public static AUserBuilder defaultUser() {
        return new AUserBuilder();
    }

    public AUserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AUserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public AUserBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public AUserBuilder withGender(boolean gender) {
        this.gender = gender;
        return this;
    }

    public AUser build() {
        AUser user = new AUser(name, password);
        user.setAge(age);
        user.setGender(gender);
        return user;
    }
}

