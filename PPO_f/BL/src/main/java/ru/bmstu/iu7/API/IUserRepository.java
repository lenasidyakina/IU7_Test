package ru.bmstu.iu7.API;


import ru.bmstu.iu7.API.model.AUser;

import java.util.List;

public interface IUserRepository {
    AUser createUser(String name, String password, int age, boolean gender) throws Exception;
    AUser findUser(String name) throws Exception;
    void delete(Long id) throws Exception;
    AUser update(AUser user) throws Exception;
    List<AUser> findAll();
    AUser findById(Long id);
    AUser saveUser(AUser user);
}
