package ru.bmstu.iu7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.impl.model.Question;
import ru.bmstu.iu7.impl.model.Questionnaire;
import ru.bmstu.iu7.impl.model.Tag;
import ru.bmstu.iu7.testdata.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Random.class)
class QuestionnaireRepositoryTest {

    @Mock
    private DBAPI api;

    @Mock
    ru.bmstu.iu7.impl.SpringQuestionnaireRepository questionnaireRepo;

    @Mock
    ru.bmstu.iu7.impl.SpringQuestionRepository questionRepo;

    @Mock
    ru.bmstu.iu7.impl.SpringTagRepository tagRepo;

    @InjectMocks
    QuestionnaireRepository repository;

    AUser lena;
    AQuestionnaire q1;
    AQuestionnaire q2;
    Questionnaire q_entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lena = UserMother.lenaAUser();
        q1 = QuestionnaireMother.lenaQuestionnaire();
        q2 = QuestionnaireMother.ivanQuestionnaire();
        q_entity = QuestionnaireMother.lenaQuestionnaireEntity();

        api.m_questionnaireRepository = questionnaireRepo;
        api.m_questionRepository = questionRepo;
        api.m_tagRepository = tagRepo;
    }

    @Test
    void findUserQuestionnaires_success() {
        when(questionnaireRepo.findByUserId(lena.getId()))
                .thenReturn((List.of(QuestionnaireMother.lenaQuestionnaireEntity())));

        List<AQuestionnaire> result = repository.findUserQuestionnaires(lena.getId());

        assertEquals(1, result.size());
    }

    @Test
    void findUserQuestionnaires_failure_returnsEmptyList() {
        when(questionnaireRepo.findByUserId(lena.getId())).thenThrow(new RuntimeException("DB error"));

        List<AQuestionnaire> result = repository.findUserQuestionnaires(lena.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void createQuestionnaire_success() {
        when(questionnaireRepo.save(any())).thenAnswer(invocation -> {
            var q = invocation.getArgument(0);
            ((Questionnaire) q).setId(1L);
            return q;
        });

        AQuestionnaire result = repository.createQuestionnaire(lena, q1.getInformation(), q1.getSearchInformation());

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void createQuestionnaire_failure() {
        when(questionnaireRepo.save(any())).thenThrow(new RuntimeException("DB error"));

        AQuestionnaire result = repository.createQuestionnaire(lena, q1.getInformation(), q1.getSearchInformation());

        assertNull(result);
    }

    @Test
    void update_success() {
        when(questionnaireRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AQuestionnaire result = repository.update(q1);

        assertNotNull(result);
        assertEquals(q1.getId(), result.getId());
    }

    @Test
    void update_failure_returnsNull() {
        when(questionnaireRepo.save(any(Questionnaire.class))).thenThrow(new RuntimeException("DB error"));

        AQuestionnaire result = repository.update(q1);

        assertNull(result);
    }

    @Test
    void delete_success() {
        doNothing().when(questionnaireRepo).delete(any());

        assertDoesNotThrow(() -> repository.delete(q1));

        verify(questionnaireRepo).delete(any());
    }

    @Test
    void delete_checkConvert() {
        repository.delete(q1);

        verify(questionnaireRepo).delete(q_entity);
    }


    @Test
    void get_page_success() throws Exception {
        when(questionnaireRepo.findAllByUserIdNot(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(QuestionnaireMother.lenaQuestionnaireEntity())));

        List<AQuestionnaire> page = repository.get_page(0, 10, lena.getId());

        assertEquals(1, page.size());
    }

    @Test
    void get_page_failure_returnsEmptyList() throws Exception {
        when(questionnaireRepo.findAllByUserIdNot(anyLong(), any())).thenThrow(new RuntimeException("DB error"));

        List<AQuestionnaire> page = repository.get_page(0, 10, lena.getId());

        assertTrue(page.isEmpty());
    }

    @Test
    void findQuestionnaire_success() {
        when(questionnaireRepo.findById(0L))
                .thenReturn(Optional.of(QuestionnaireMother.lenaQuestionnaireEntity()));

        AQuestionnaire result = repository.findQuestionnaire(0L);

        assertNotNull(result);
        assertEquals(0L, result.getId());
    }

    @Test
    void findQuestionnaire_notFound_returnsNull() {
        when(questionnaireRepo.findById(0L)).thenReturn(Optional.empty());

        AQuestionnaire result = repository.findQuestionnaire(0L);

        assertNull(result);
    }

    @Test
    void get_all_questions_failure_returnsEmpty() {
        when(questionRepo.findAll()).thenThrow(new RuntimeException("DB error"));

        List<AQuestion> questions = repository.get_all_questions();

        assertTrue(questions.isEmpty());
    }

    @Test
    void deleteQuestionnaire_success() {
        doNothing().when(questionnaireRepo).deleteByIdNative(anyLong());

        assertDoesNotThrow(() -> repository.deleteQuestionnaire(q1));

        verify(questionnaireRepo).deleteByIdNative(q1.getId());
    }

    @Test
    void deleteQuestionnaire() {
        repository.deleteQuestionnaire(q1);

        verify(questionnaireRepo).deleteByIdNative(q1.getId());
    }

    @Test
    void get_all_questionnaires_success() {
        when(questionnaireRepo.findAll()).thenReturn(List.of(QuestionnaireMother.lenaQuestionnaireEntity()));

        List<AQuestionnaire> all = repository.get_all_questionnaires();

        assertEquals(1, all.size());
    }

    @Test
    void get_all_questionnaires_failure_returnsEmpty() {
        when(questionnaireRepo.findAll()).thenThrow(new RuntimeException("DB error"));

        List<AQuestionnaire> all = repository.get_all_questionnaires();

        assertTrue(all.isEmpty());
    }

    @Test
    void deleteUserQuestionnaires_success() {
        doNothing().when(questionnaireRepo).deleteByUserId(anyLong());

        assertDoesNotThrow(() -> repository.deleteUserQuestionnaires(lena.getId()));

        verify(questionnaireRepo).deleteByUserId(lena.getId());
    }

    @Test
    void get_all_questions_success() {
        when(questionRepo.findAll()).thenReturn(List.of(QuestionMother.extendedQuestion()));

        List<AQuestion> questions = repository.get_all_questions();

        assertEquals(1, questions.size());
    }


    @Test
    void appendQuestion_newQuestion() {
        String text = "Do you like Java?";
        List<ATag> tags = List.of(TagMother.defaultATag());
        when(questionRepo.findByQuestion(text)).thenReturn(null);
        when(questionRepo.save(any(Question.class))).thenAnswer(invocation -> {
            Question q = invocation.getArgument(0);
            q.setId(1L);
            return q;
        });

        AQuestion q = repository.appendQuestion(true, text, tags);

        assertNotNull(q);
        assertEquals(1L, q.getId());
    }

    @Test
    void appendQuestion_existingQuestion() {
        Question existing = QuestionMother.extendedQuestion();
        when(questionRepo.findByQuestion(existing.getQuestion())).thenReturn(existing);

        AQuestion q = repository.appendQuestion(existing.getIs_extended(), existing.getQuestion(), List.of());

        assertNotNull(q);
        assertEquals(existing.getId(), q.getId());
    }


    @Test
    void appendTag_newTag() {
        when(tagRepo.findByName("newTag")).thenReturn(null);
        when(tagRepo.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        ATag tag = repository.appendTag("newTag");

        assertNotNull(tag);
        assertEquals(1L, tag.getId());
    }

    @Test
    void appendTag_existingTag() {
        Tag existing = TagMother.defaultTag();
        when(tagRepo.findByName(existing.getName())).thenReturn(existing);

        ATag tag = repository.appendTag(existing.getName());

        assertNotNull(tag);
        assertEquals(existing.getId(), tag.getId());
    }

    @Test
    void findTag_found() {
        Tag t = TagMother.defaultTag();
        when(tagRepo.findByName(t.getName())).thenReturn(t);

        ATag tag = repository.findTag(t.getName());

        assertNotNull(tag);
        assertEquals(t.getId(), tag.getId());
    }

    @Test
    void findTag_notFound_returnsNull() {
        when(tagRepo.findByName("unknown")).thenReturn(null);

        ATag tag = repository.findTag("unknown");

        assertNull(tag);
    }
}
