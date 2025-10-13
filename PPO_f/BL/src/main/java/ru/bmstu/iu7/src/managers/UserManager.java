package ru.bmstu.iu7.src.managers;

import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.IUserRepository;
import ru.bmstu.iu7.API.model.AUser;

public class UserManager {
    private final IUserRepository m_user_repository;
    private final AppLogger logger;

    public UserManager(IUserRepository userRepository, AppLogger applogger) {
        this.m_user_repository = userRepository;
        this.logger = applogger;
        if (logger != null) logger.info("UserManager initialized");
    }

    public UserManager(IUserRepository userRepository) {
        this(userRepository, null);
    }

    public AUser register(String username, String password, int age, boolean gender) {
        try {
            if (logger != null) logger.info("Attempting to register user: {}", username);
            if (m_user_repository.findUser(username) == null) {
                AUser user = m_user_repository.createUser(username, password, age, gender);
                System.out.println("User registered successfully:");
                if (logger != null) logger.info("User registered successfully: {}", username);
                return user;
            } else {
                System.out.println("user already exists");
                if (logger != null) logger.warn("Registration failed, user already exists: {}", username);
                return null;
            }
        } catch (Exception e) {
            System.out.println("error");
            if (logger != null) logger.error("Failed to register user {}: {}", username, e.getMessage(), e);
            return null;
        }
    }

    public AUser authorize(String username, String password) {
        try {
            if (logger != null) logger.info("Attempting to authorize user: {}", username);
            AUser u = m_user_repository.findUser(username);
            if (u == null) {
                if (logger != null) logger.warn("Authorization failed, user not found: {}", username);
                return null;
            }
            if (u.getPassword().equals(password)) {
                if (logger != null) logger.info("User authorized successfully: {}", username);
                return u;
            } else {
                if (logger != null) logger.warn("Authorization failed, incorrect password for user: {}", username);
                return null;
            }
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to authorize user {}: {}", username, e.getMessage(), e);
            return null;
        }
    }

    public void delete(Long Id) {
        try {
            if (logger != null) logger.info("Deleting user with ID: {}", Id);
            m_user_repository.delete(Id);
            if (logger != null) logger.info("User deleted with ID: {}", Id);
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to delete user with ID {}: {}", Id, e.getMessage(), e);
        }
    }
}
