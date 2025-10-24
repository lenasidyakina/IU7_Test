package ru.bmstu.iu7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bmstu.iu7.API.IUserRepository;
import ru.bmstu.iu7.API.model.AUser;
import ru.bmstu.iu7.impl.ModelFactory;
import ru.bmstu.iu7.impl.SpringUserRepository;
import ru.bmstu.iu7.impl.model.User;

import java.util.List;

public class UserRepository implements IUserRepository {

    private final SpringUserRepository m_springUserRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository(SpringUserRepository springUserRepository) {
        this.m_springUserRepository = springUserRepository;
        logger.info("UserRepository initialized");
    }

    @Override
    public AUser createUser(String name, String password, int age, boolean gender)  {
        try {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
            if (age <= 0) throw new IllegalArgumentException("Age must be positive");

            logger.info("Creating user with name: {}", name);
            User u = new User(name, password, age, gender, "user");
            u = m_springUserRepository.save(u);
            logger.info("User created with id: {}", u.getId());
            return ModelFactory.User2AUser(u);
        } catch (Exception e) {
            logger.error("Failed to create user '{}': {}", name, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AUser saveUser(AUser user)  {
        try {
            User u = m_springUserRepository.save(ModelFactory.AUser2User(user));
            logger.info("User saved with id: {}", u.getId());
            return ModelFactory.User2AUser(u);
        } catch (Exception e) {
            logger.error("Failed to create user '{}': {}", user.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AUser findUser(String name)  {
        try {
            logger.info("Finding user with name: {}", name);
            User u = m_springUserRepository.findByName(name);
            if (u != null) {
                logger.info("User found with id: {}", u.getId());
            } else {
                logger.warn("User not found: {}", name);
            }
            return ModelFactory.User2AUser(u);
        } catch (Exception e) {
            logger.error("Failed to find user '{}': {}", name, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        try {
            logger.info("Deleting user with id: {}", id);
            m_springUserRepository.deleteById(id);
            logger.info("User deleted with id: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete user with id '{}': {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AUser update(AUser user)  {
        try {
            logger.info("Updating user with id: {}", user.getId());
            User u = m_springUserRepository.save(ModelFactory.AUser2User(user));
            logger.info("User updated with id: {}", user.getId());
            return ModelFactory.User2AUser(u);
        } catch (Exception e) {
            logger.error("Failed to update user with id '{}': {}", user.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AUser> findAll() {
        try {
            logger.info("Fetching all users");
            return ModelFactory.Users2AUsers(m_springUserRepository.findAll());
        } catch (Exception e) {
            logger.error("Failed to fetch all users: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public AUser findById(Long id) {
        try {
            logger.info("Fetching user with id {}", id);
            return m_springUserRepository.findById(id)
                    .map(ModelFactory::User2AUser)  // если найден, преобразуем
                    .orElse(null);                   // если нет — возвращаем null
        } catch (Exception e) {
            logger.error("Failed to fetch user with id {}: {}", id, e.getMessage(), e);
            return null;
        }
    }


}
