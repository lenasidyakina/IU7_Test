package ru.bmstu.iu7.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.iu7.impl.model.Questionnaire;
import java.util.List;



@Repository
public interface SpringQuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
    List<Questionnaire> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    @Query(value = "SELECT * FROM recommend_questionnaires(:questionnaireId)", nativeQuery = true)
    List<Long> findRecommendedQuestionnaireIds(@Param("questionnaireId") Long questionnaireId);
    Page<Questionnaire> findAllByUserIdNot(Long userId, PageRequest pageable);
    List<Questionnaire> findAll();
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM questionnaire WHERE id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") Long id);
}
