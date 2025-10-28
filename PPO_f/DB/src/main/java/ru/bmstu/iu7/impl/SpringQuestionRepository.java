package ru.bmstu.iu7.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.iu7.impl.model.Question;

@Repository
public interface SpringQuestionRepository extends JpaRepository<Question, Long>{
    Question findByQuestion(String name);
}
