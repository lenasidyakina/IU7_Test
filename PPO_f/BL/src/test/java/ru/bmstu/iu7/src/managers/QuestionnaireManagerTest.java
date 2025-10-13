package ru.bmstu.iu7.src.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bmstu.iu7.API.IML_port;
import ru.bmstu.iu7.API.IQuestionnaireRepository;
import ru.bmstu.iu7.API.IReqCacheRepository;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.src.controllers.QuestionnaireController;
import ru.bmstu.iu7.src.testdata.QuestionnaireMother;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(MockitoExtension.class)
class QuestionnaireManagerTest {

    @Mock
    IML_port iml_port;

    @Mock
    IQuestionnaireRepository quest_repository;

    @Mock
    IReqCacheRepository req_cache_repository;

    private QuestionnaireController controller;
    private QuestionnaireManager manager;
    private QuestionnaireController spyController;
    private QuestionnaireManager spyManager;
    private AQuestionnaire quest;

    @BeforeEach
    void setUp() {
        controller = new QuestionnaireController(iml_port, quest_repository, req_cache_repository);
        manager = new QuestionnaireManager(controller);

        spyController = spy(controller);
        spyManager = new QuestionnaireManager(spyController);

        quest = QuestionnaireMother.lenaQuestionnaire();
        controller.set_active_questionnaire(quest);
    }

    @Test
    void setActiveQuestionnaire_positive() {
        manager.set_active_questionnaire(quest);

        assertEquals(quest, controller.get_active_questionnaire());
    }

    @Test
    void setActiveQuestionnaire_exceptionHandling() {
        AQuestionnaire nullQuest = null;

        assertDoesNotThrow(() -> manager.set_active_questionnaire(nullQuest));
    }

    @Test
    void create_positive() {
        AUser user = quest.getUser();
        AInformation info = quest.getInformation();
        AInformation searchInfo = quest.getSearchInformation();

        manager.create(user, info, searchInfo);

        verify(quest_repository, atLeast(0)).createQuestionnaire(user, info, searchInfo);
    }

    @Test
    void create_nullUser_returnsNull() {
        AInformation info = quest.getInformation();
        AInformation searchInfo = quest.getSearchInformation();

        AQuestionnaire result = manager.create(null, info, searchInfo);

        assertNull(result);
    }

    @Test
    void censor_positive_classic() {
        manager.censor(quest);

        assertTrue(quest.isCensored());
    }

    @Test
    void censor_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).censor(quest);

        assertDoesNotThrow(() -> spyManager.censor(quest));
    }

    @Test
    void getAllQuestions_positive() {
        List<AQuestion> questions = manager.get_all_questions();

        assertNotNull(questions);
    }

    @Test
    void getAllQuestions_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).get_all_questions();

        List<AQuestion> questions = spyManager.get_all_questions();

        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    @Test
    void getAllQuestionnaires_positive() {
        List<AQuestionnaire> list = manager.get_all_questionnaires();

        assertNotNull(list);
    }

    @Test
    void getAllQuestionnaires_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).get_all_questionnaires();

        List<AQuestionnaire> list = spyManager.get_all_questionnaires();

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void findQuestionnaire_positive() throws Exception {
        AQuestionnaire quest = QuestionnaireMother.lenaQuestionnaire();
        QuestionnaireController mockController = Mockito.mock(QuestionnaireController.class);
        Mockito.lenient().doReturn(quest).when(mockController).findQuestionnaire(quest.getId());
        QuestionnaireManager manager = new QuestionnaireManager(mockController);

        AQuestionnaire found = manager.findQuestionnaire(quest.getId());

        assertEquals(quest, found);
    }

    @Test
    void findQuestionnaire_notFound_returnsNull() {
        AQuestionnaire found = manager.findQuestionnaire(999L);

        assertNull(found);
    }

    @Test
    void addFav_positive() {
        assertDoesNotThrow(() -> manager.add_fav(quest));
    }

    @Test
    void addFav_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).add_fav(quest);

        assertDoesNotThrow(() -> spyManager.add_fav(quest));
    }

    @Test
    void delFav_positive() {
        assertDoesNotThrow(() -> manager.del_fav(quest));
    }

    @Test
    void delFav_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).del_fav(quest);

        assertDoesNotThrow(() -> spyManager.del_fav(quest));
    }

    @Test
    void addBlack_positive() {
        assertDoesNotThrow(() -> manager.add_black(quest));
    }

    @Test
    void addBlack_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).add_black(quest);

        assertDoesNotThrow(() -> spyManager.add_black(quest));
    }

    @Test
    void delBlack_positive() {
        assertDoesNotThrow(() -> manager.del_black(quest));
    }

    @Test
    void delBlack_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).del_black(quest);

        assertDoesNotThrow(() -> spyManager.del_black(quest));
    }

    @Test
    void delete_positive() {
        assertDoesNotThrow(() -> manager.delete(quest));
    }

    @Test
    void delete_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).delete(quest);

        assertDoesNotThrow(() -> spyManager.delete(quest));
    }

    @Test
    void deleteQuestionnaire_positive() {
        assertDoesNotThrow(() -> manager.delete_questionnaire(quest));
    }

    @Test
    void deleteQuestionnaire_exceptionHandling() {
        doThrow(new RuntimeException("error")).when(spyController).delete_questionnaire(quest);

        assertDoesNotThrow(() -> spyManager.delete_questionnaire(quest));
    }

    @Test
    void deleteUserQuestionnaires_positive() {
        assertDoesNotThrow(() -> manager.delete_user_questionnaires(quest.getUser().getId()));
    }

    @Test
    void deleteUserQuestionnaires_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController)
                .delete_user_questionnaires(quest.getUser().getId());

        assertDoesNotThrow(() -> spyManager.delete_user_questionnaires(quest.getUser().getId()));
    }


    @Test
    void appendQuestion_positive() throws Exception {
        QuestionnaireController controller = mock(QuestionnaireController.class);
        QuestionnaireManager manager = new QuestionnaireManager(controller);
        ATag tag = new ATag(1L, "tag");
        List<ATag> tags = List.of(tag);

        manager.append_question(true, "Test question", tags);

        verify(controller).append_question(true, "Test question", tags);
    }

    @Test
    void appendQuestion_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController)
                .append_question(true, "Test question", List.of(new ATag(1L, "tag")));

        AQuestion question = spyManager.append_question(true, "Test question", List.of(new ATag(1L, "tag")));

        assertNull(question);
    }

    @Test
    void appendTag_positive() throws Exception {
        ATag expected = new ATag();
        expected.setName("tag");
        when(controller.append_tag("tag")).thenReturn(expected);

        ATag tag = manager.append_tag("tag");

        assertNotNull(tag);
        assertEquals("tag", tag.getName());
    }

    @Test
    void appendTag_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).append_tag("tag");

        ATag tag = spyManager.append_tag("tag");

        assertNull(tag);
    }

    @Test
    void findTag_positive() throws Exception {
        ATag walking = new ATag("walking");
        QuestionnaireController mockController = Mockito.mock(QuestionnaireController.class);
        Mockito.when(mockController.find_tag("walking")).thenReturn(walking);
        QuestionnaireManager manager = new QuestionnaireManager(mockController);

        ATag found = manager.find_tag("walking");

        assertNotNull(found);
        assertEquals("walking", found.getName());
    }

    @Test
    void findTag_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).find_tag("walking");

        ATag tag = spyManager.find_tag("walking");

        assertNull(tag);
    }

    @Test
    void clearReqCache_positive() {
        assertDoesNotThrow(() -> manager.clear_req_cache());
    }

    @Test
    void clearReqCache_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController).clear_req_cache();

        assertDoesNotThrow(() -> spyManager.clear_req_cache());
    }

    @Test
    void getUserQuestionnaires_positive() {
        List<AQuestionnaire> list = manager.get_user_questionnaies(quest.getUser().getId());

        assertNotNull(list);
    }

    @Test
    void getUserQuestionnaires_exceptionHandling() throws Exception {
        doThrow(new RuntimeException("error")).when(spyController)
                .get_user_questionnaies(quest.getUser().getId());

        List<AQuestionnaire> list = spyManager.get_user_questionnaies(quest.getUser().getId());

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}
