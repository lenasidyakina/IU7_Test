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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(MockitoExtension.class)
class RecManagerTest {

    @Mock
    IML_port iml_port;
    @Mock
    IQuestionnaireRepository questionnaire_repository;
    @Mock
    IReqCacheRepository req_cache_repository;

    private QuestionnaireController controller;
    private RecManager recManager;
    private AQuestionnaire activeQuest;
    private List<AQuestionnaire> cache;

    @BeforeEach
    void setUp() throws Exception {
        controller = Mockito.spy(new QuestionnaireController(iml_port, questionnaire_repository, req_cache_repository));
        activeQuest = QuestionnaireMother.lenaQuestionnaire();
        controller.set_active_questionnaire(activeQuest);
        cache = new ArrayList<>();
        Mockito.lenient().doReturn(cache).when(controller).get_quest_in_cache();
        recManager = new RecManager(controller);
    }

    @Test
    void getFriends_positive() {
        AQuestionnaire other = QuestionnaireMother.iraQuestionnaire();
        cache.add(other);

        List<AQuestionnaire> friends = recManager.get_friends();

        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals(other, friends.get(0));
    }

    @Test
    void getFriends_noActiveQuestionnaire() {
        controller.set_active_questionnaire(null);

        List<AQuestionnaire> friends = recManager.get_friends();

        assertNotNull(friends);
        assertTrue(friends.isEmpty());
    }

    @Test
    void information_comparison_equalVariantAnswers() {
        AInformation info1 = QuestionnaireMother.lenaQuestionnaire().getInformation();
        AInformation info2 = QuestionnaireMother.lenaQuestionnaire().getSearchInformation();

        int coeff = recManager.information_comparison(info1, info2);

        assertEquals(2, coeff);
    }

    @Test
    void information_comparison_differentVariantAnswers() {
        AInformation info1 = QuestionnaireMother.lenaQuestionnaire().getInformation();
        AInformation info2 = QuestionnaireMother.differentVariantQuestionnaire().getSearchInformation();

        int coeff = recManager.information_comparison(info1, info2);

        assertEquals(2, coeff);
    }

    @Test
    void information_comparison_fullMatch() {
        AInformation info1 = QuestionnaireMother.fullMatchQuestionnaire().getInformation();
        AInformation info2 = QuestionnaireMother.fullMatchQuestionnaireOther().getSearchInformation();

        int coeff = recManager.information_comparison(info1, info2);

        assertEquals(4, coeff);
    }

    @Test
    void recommended_questionnaires_positive() throws Exception {
        AQuestionnaire other = QuestionnaireMother.iraQuestionnaire();
        controller = Mockito.mock(QuestionnaireController.class);
        List<AQuestionnaire> cache = new ArrayList<>();
        Mockito.lenient().doReturn(false).when(controller).is_in_black(other);
        Mockito.lenient().doReturn(false).when(controller).is_in_fav(other);
        Mockito.doAnswer(invocation -> {
            AQuestionnaire q = invocation.getArgument(1);
            cache.add(q);
            return null;
        }).when(controller).add_in_cache_list(Mockito.anyDouble(), Mockito.any(AQuestionnaire.class));
        recManager = new RecManager(controller);

        recManager.recommended_questionnaires(activeQuest, List.of(other));

        assertEquals(1, cache.size());
        assertEquals(other, cache.get(0));
    }


    @Test
    void recommended_questionnaires_skipSelf() throws Exception {
        recManager.recommended_questionnaires(activeQuest, List.of(activeQuest));

        List<AQuestionnaire> friends = controller.get_quest_in_cache();
        assertTrue(friends.isEmpty());
    }

    @Test
    void recommended_questionnaires_exceptionHandling() throws Exception {
        assertDoesNotThrow(() -> recManager.recommended_questionnaires(activeQuest, null));

        assertTrue(controller.get_quest_in_cache().isEmpty());
    }

    @Test
    void doGetFriends_positive_single() throws Exception {
        AQuestionnaire other = QuestionnaireMother.iraQuestionnaire();
        List<AQuestionnaire> testCache = new ArrayList<>();
        controller = Mockito.mock(QuestionnaireController.class);
        Mockito.lenient().doReturn(activeQuest).when(controller).get_active_questionnaire();
        Mockito.lenient().doReturn(List.of(other))
                .when(controller).get_questionnairies(0, 1000, activeQuest.getUser().getId());
        Mockito.lenient().doReturn(false).when(controller).is_in_black(other);
        Mockito.lenient().doReturn(false).when(controller).is_in_fav(other);
        Mockito.doAnswer(invocation -> {
            AQuestionnaire q = invocation.getArgument(1);
            testCache.add(q);
            return null;
        }).when(controller).add_in_cache_list(Mockito.anyDouble(), Mockito.any(AQuestionnaire.class));
        recManager = new RecManager(controller);

        recManager.doGetFriends();

        assertNotNull(testCache);
        assertEquals(1, testCache.size());
        assertEquals(other, testCache.get(0));
    }


    @Test
    void doGetFriends_empty() throws Exception {
        Mockito.lenient().doReturn(List.of())
                .when(controller).get_questionnairies(0, 1000, activeQuest.getUser().getId());
        Mockito.lenient().doReturn(List.of())
                .when(req_cache_repository).findAll(activeQuest);

        recManager.doGetFriends();

        assertTrue(controller.get_quest_in_cache().isEmpty());
    }

    @Test
    void doGetFriends_exception_handling() throws Exception {
        Mockito.lenient().doThrow(new RuntimeException("Test Exception"))
                .when(controller).get_questionnairies(0, 1000, activeQuest.getUser().getId());

        assertDoesNotThrow(() -> recManager.doGetFriends());

        assertTrue(controller.get_quest_in_cache().isEmpty());
    }
}
