package ru.bmstu.iu7.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.iu7.impl.model.User;

@Repository
public interface SpringUserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
}
