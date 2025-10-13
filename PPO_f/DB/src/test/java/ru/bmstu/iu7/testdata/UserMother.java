package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.API.model.AUser;
import ru.bmstu.iu7.impl.model.User;

public class UserMother {

    public static User lenaUser() {
        return UserBuilder.defaultUser()
                .withId(0L)
                .build();
    }


    public static AUser lenaAUser() {
        return AUserBuilder.defaultAUser()
                .withId(0L)
                .build();
    }
}
