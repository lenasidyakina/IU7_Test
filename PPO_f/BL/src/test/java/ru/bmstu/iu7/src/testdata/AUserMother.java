package ru.bmstu.iu7.src.testdata;

import ru.bmstu.iu7.API.model.AUser;

public class AUserMother {

    public static AUser lenaAUser() {
        return new AUser("Lena", "Lena12345");
    }

    public static AUser ivanAUser() {
        return new AUserBuilder().withName("Ivan").build();
    }

}