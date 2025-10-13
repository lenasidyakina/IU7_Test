package ru.bmstu.iu7.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.iu7.impl.model.ExtendedAnswer;

@Repository
public interface SpringExtendedAnswerRepository extends JpaRepository<ExtendedAnswer, Long>{
}
