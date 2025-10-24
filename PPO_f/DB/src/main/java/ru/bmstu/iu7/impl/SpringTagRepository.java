package ru.bmstu.iu7.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.iu7.impl.model.Tag;

@Repository
public interface SpringTagRepository extends JpaRepository<Tag, Long>{
    Tag findByName(String name);
}
