import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import ru.bmstu.iu7.*;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.impl.*;
import ru.bmstu.iu7.src.controllers.QuestionnaireController;
import ru.bmstu.iu7.src.managers.QuestionnaireManager;
import ru.bmstu.iu7.src.managers.UserManager;
import ru.bmstu.iu7.src.managers.RecManager;
import ru.bmstu.iu7.DBAPI;


import java.util.ArrayList;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureDataJdbc
@EnableJdbcRepositories(basePackages = "ru.bmstu.iu7")
@ContextConfiguration(classes = {
        SpringQuestionnaireRepository.class,
        SpringExtendedAnswerRepository.class,
        SpringInformationRepository.class,
        SpringQuestionRepository.class,
        SpringReqCacheRepository.class,
        SpringTagRepository.class,
        SpringUserRepository.class,
        SpringVariantAnswerRepository.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
})

@ComponentScan("ru.bmstu.iu7")
@Transactional
public class DatabaseTest {

    @Autowired private SpringUserRepository userRepository;
    @Autowired private SpringReqCacheRepository reqCacheRepository;
    @Autowired private SpringQuestionnaireRepository questionnaireRepository;
    @Autowired private SpringExtendedAnswerRepository extendedAnswerRepository;
    @Autowired private SpringVariantAnswerRepository variantAnswerRepository;
    @Autowired private SpringQuestionRepository questionRepository;
    @Autowired private SpringTagRepository tagRepository;
    @Autowired private SpringInformationRepository informationRepository;

    // -------------------- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ --------------------

    private DBAPI buildDBAPI() {
        DBAPI api = new DBAPI();
        api.m_extendedAnswerRepository = extendedAnswerRepository;
        api.m_questionnaireRepository = questionnaireRepository;
        api.m_variantAnswerRepository = variantAnswerRepository;
        api.m_userRepository = userRepository;
        api.m_informationRepository = informationRepository;
        api.m_questionRepository = questionRepository;
        api.m_tagRepository = tagRepository;
        api.m_reqCacheRepository = reqCacheRepository;
        return api;
    }

    private record QuestionnaireComponents(
            QuestionnaireManager manager,
            QuestionnaireController controller
    ) {}

    private QuestionnaireComponents buildQuestionnaireComponents(DBAPI api) {
        var xquestRepository = new QuestionnaireRepository(api);
        var xreqCacheRepository = new ReqCacheRepository(reqCacheRepository);
        var questionnaireController =
                new QuestionnaireController(new ML_port("http://localhost:11434/api/generate"),
                        xquestRepository, xreqCacheRepository);
        var questionnaireManager = new QuestionnaireManager(questionnaireController);
        return new QuestionnaireComponents(questionnaireManager, questionnaireController);
    }

    private UserManager buildUserManager(UserRepository repo) {
        return new UserManager(repo);
    }

    private AInformation createInformation(QuestionnaireManager questionnaireManager, String shortAnswer, String longAnswer) {
        List<AVariantAnswer> variantAnswers = new ArrayList<>();
        List<AExtendedAnswer> extendedAnswers = new ArrayList<>();

        for (AQuestion q : questionnaireManager.get_all_questions()) {
            int weight = 1;
            if (!q.getIs_extended()) {
                ATag tag = questionnaireManager.find_tag(shortAnswer);
                variantAnswers.add(new AVariantAnswer(weight, tag, q));
            } else {
                extendedAnswers.add(new AExtendedAnswer(q, weight, longAnswer, new ArrayList<>()));
            }
        }

        return new AInformation(variantAnswers, extendedAnswers);
    }

    private AQuestionnaire createQuestionnaire(QuestionnaireManager questionnaireManager, AUser user,
                                               String shortAnswer1, String longAnswer1,
                                               String shortAnswer2, String longAnswer2) throws Exception {
        AInformation info1 = createInformation(questionnaireManager, shortAnswer1, longAnswer1);
        AInformation info2 = createInformation(questionnaireManager, shortAnswer2, longAnswer2);
        return questionnaireManager.create(user, info2, info1);
    }

    // -------------------- ТЕСТЫ --------------------

    @Test
    public void register() throws Exception {
        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        AUser expected = new AUser("Lena", "Lena12345");
        userManager.register("Lena", "Lena12345", 20, true);

        AUser actual = xuserRepository.findUser("Lena");
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void authorize() throws Exception {
        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        userManager.register("Lenaa", "Lena12345", 20, true);
        AUser expected = xuserRepository.findUser("Lenaa");

        AUser actual = userManager.authorize("Lenaa", "Lena12345");
        Assertions.assertEquals(expected.getId(), actual.getId());
    }

    @Test
    public void get_friends() throws Exception {
        DBAPI api = buildDBAPI();
        var components = buildQuestionnaireComponents(api);
        QuestionnaireManager questionnaireManager = components.manager();
        var questionnaireController = components.controller();
        var reqManager = new RecManager(questionnaireController);

        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        AUser user1 = userManager.register("LenaMain", "12345", 20, true);
        AQuestionnaire q1 = createQuestionnaire(questionnaireManager, user1,
                "watching TV", "I like walking my dog and sleeping.",
                "swimming", "I like walking my dog.");

        AUser user2 = userManager.register("LenaFriend", "12345", 20, true);
        createQuestionnaire(questionnaireManager, user2,
                "swimming", "I like walking my dog.",
                "swimming", "I like walking my dog.");

        questionnaireController.set_active_questionnaire(q1);
        reqManager.doGetFriends();

        List<AQuestionnaire> friends = questionnaireController.get_quest_in_cache();
        Assertions.assertEquals("LenaFriend", friends.getFirst().getUser().getName());
    }

    @Test
    public void add_fav_list() throws Exception {
        DBAPI api = buildDBAPI();
        var components = buildQuestionnaireComponents(api);
        QuestionnaireManager questionnaireManager = components.manager();
        var questionnaireController = components.controller();
        var reqManager = new RecManager(questionnaireController);

        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        AUser user1 = userManager.register("LenaFav", "12345", 20, true);
        AQuestionnaire q1 = createQuestionnaire(questionnaireManager, user1,
                "watching TV", "I like walking my dog and sleeping.",
                "swimming", "I like walking my dog.");

        AUser user2 = userManager.register("LenaFriend", "12345", 20, true);
        createQuestionnaire(questionnaireManager, user2,
                "swimming", "I like walking my dog.",
                "swimming", "I like walking my dog.");

        questionnaireController.set_active_questionnaire(q1);
        reqManager.doGetFriends();

        AQuestionnaire friend = questionnaireController.get_quest_in_cache().getFirst();
        questionnaireManager.add_fav(friend);
        reqManager.doGetFriends();

        List<AQuestionnaire> updatedFriends = questionnaireController.get_quest_in_cache();
        Assertions.assertEquals(3, updatedFriends.size());
        Assertions.assertEquals(q1.getFavList().getFirst().getId(), friend.getId());
    }

    @Test
    public void add_black_list() throws Exception {
        DBAPI api = buildDBAPI();
        var components = buildQuestionnaireComponents(api);
        QuestionnaireManager questionnaireManager = components.manager();
        var questionnaireController = components.controller();
        var reqManager = new RecManager(questionnaireController);

        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        AUser user1 = userManager.register("LenaBlack", "12345", 20, true);
        AQuestionnaire q1 = createQuestionnaire(questionnaireManager, user1,
                "watching TV", "I like walking my dog and sleeping.",
                "swimming", "I like walking my dog.");

        AUser user2 = userManager.register("LenaBlocked", "12345", 20, true);
        createQuestionnaire(questionnaireManager, user2,
                "swimming", "I like walking my dog.",
                "swimming", "I like walking my dog.");

        questionnaireController.set_active_questionnaire(q1);
        reqManager.doGetFriends();

        AQuestionnaire friend = questionnaireController.get_quest_in_cache().getFirst();
        questionnaireManager.add_black(friend);
        reqManager.doGetFriends();

        Assertions.assertEquals(0, questionnaireController.get_quest_in_cache().size());
        Assertions.assertEquals(q1.getBlackList().getFirst().getId(), friend.getId());
    }

    @Test
    public void del_black_list() throws Exception {
        DBAPI api = buildDBAPI();
        var components = buildQuestionnaireComponents(api);
        QuestionnaireManager questionnaireManager = components.manager();

        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        AUser user = userManager.authorize("LenaBlack", "12345");
        AQuestionnaire q = questionnaireManager.get_user_questionnaies(user.getId()).getFirst();

        components.controller().set_active_questionnaire(q);
        questionnaireManager.del_black(q.getBlackList().getFirst());

        Assertions.assertEquals(0, q.getBlackList().size());
    }

    @Test
    public void del_fav_list() throws Exception {
        DBAPI api = buildDBAPI();
        var components = buildQuestionnaireComponents(api);
        QuestionnaireManager questionnaireManager = components.manager();

        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        AUser user = userManager.authorize("LenaFav", "12345");
        AQuestionnaire q = questionnaireManager.get_user_questionnaies(user.getId()).getFirst();

        components.controller().set_active_questionnaire(q);
        questionnaireManager.del_fav(q.getFavList().getFirst());

        Assertions.assertEquals(0, q.getFavList().size());
    }

    @Test
    @DatabaseSetup("classpath:db_test_data.xml")
    public void create() throws Exception {
        DBAPI api = buildDBAPI();
        var components = buildQuestionnaireComponents(api);
        QuestionnaireManager questionnaireManager = components.manager();

        UserRepository xuserRepository = new UserRepository(userRepository);
        UserManager userManager = buildUserManager(xuserRepository);

        AUser user = userManager.register("LenaCreate", "12345", 20, true);
            
        ATag walking_tag = questionnaireManager.append_tag("walking");
        ATag watching_tag = questionnaireManager.append_tag("watching TV");
        ATag swimming_tag = questionnaireManager.append_tag("swimming");
        ATag sleeping_tag = questionnaireManager.append_tag("sleeping");

        questionnaireManager.append_question(false,
                "Do you love swimming or watching TV?", new ArrayList<>(Arrays.asList(watching_tag, swimming_tag)));
        questionnaireManager.append_question(true,
                "How do you like to spend your time?", new ArrayList<>(Arrays.asList(walking_tag, sleeping_tag)));
        
        AQuestionnaire q = createQuestionnaire(questionnaireManager, user,
                "swimming", "I like walking my dog.",
                "swimming", "I like walking my dog.");

        AQuestionnaire fromDB = new QuestionnaireRepository(api).findQuestionnaire(q.getId());
        Assertions.assertEquals(q.getUser().getName(), fromDB.getUser().getName());
    }
}
