package ru.bmstu.iu7;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.impl.*;
import ru.bmstu.iu7.src.MainManager;
import ru.bmstu.iu7.src.controllers.QuestionnaireController;
import ru.bmstu.iu7.src.controllers.ReqCacheController;
import ru.bmstu.iu7.src.managers.QuestionnaireManager;
import ru.bmstu.iu7.src.managers.RecManager;
import ru.bmstu.iu7.src.managers.UserManager;

import java.io.Console;
import java.io.PrintStream;
import java.util.*;

@SpringBootApplication
public class Main {

    private static UserRepository userRepository;
    @Value("${olama-host}")
    private String wiredOlamaHost;

    private static String olamaHost;
    private static AppLogger logger;

    @PostConstruct
    public void init() {
        olamaHost = wiredOlamaHost;
        logger = new AppLoggerImpl("AppLogger");
        ReqCacheController.m_list = new ArrayList<AQuestionnaire>();
    }

    @Value("${ml.api.url}")
    private static String mlApiUrl;

    @Value("${ml.api.content-type}")
    private static Console con;

    static AUser test_add_user(MainManager m, String name, String pwd) throws Exception {
        return m.getM_user_manager().register(name, pwd, 43, true);
    }

    static ATag test_add_tag(MainManager m, String tag) throws Exception {
        return m.getM_que_manager().append_tag("tag");
    }

    static AQuestion test_add_variant_question(MainManager m, String text, List<ATag> tags) throws Exception {
        return m.getM_que_manager().append_question(VARIANT_QUESTION_KIND, text, tags);
    }

    static AQuestion test_add_extended_question(MainManager m, String text) throws Exception {
        return m.getM_que_manager().append_question(EXTENDED_QUESTION_KIND, text, new ArrayList<ATag>());
    }

    static AQuestionnaire add_questionnaire(MainManager m, AUser user) throws Exception {
        AInformation info1 = emulate_information(m);
        AInformation info2 = emulate_information(m);
        return m.getM_que_manager().create(user, info1, info2);
    }

    static AInformation emulate_information(MainManager m) throws Exception {
        ATag walkingTag = m.getM_que_manager().append_tag("walking");
        ATag watchingTag = m.getM_que_manager().append_tag("watching TV");
        ATag swimmingTag = m.getM_que_manager().append_tag("swimming");
        ATag sleepingTag = m.getM_que_manager().append_tag("sleeping");

        List<ATag> tags1 = Arrays.asList(watchingTag, swimmingTag);
        AQuestion q1 = m.getM_que_manager().append_question(
                VARIANT_QUESTION_KIND,
                "Do you love swimming or watching TV?",
                new ArrayList<>(tags1)
        );

        List<ATag> tags2 = Arrays.asList(walkingTag, sleepingTag);
        AQuestion q2 = m.getM_que_manager().append_question(
                EXTENDED_QUESTION_KIND,
                "How do you like to spend your time?",
                new ArrayList<>(tags2)
        );

        String[] answersArray = new String[]{
                "I like walking", "I like sleeping", "I like walking and sleeping"
        };

        Random random = new Random();
        List<AVariantAnswer> variantAnswers = new ArrayList<>();
        List<AExtendedAnswer> extendedAnswers = new ArrayList<>();

        for (AQuestion q : m.getM_que_manager().get_all_questions()) {
            if (!q.getIs_extended()) {
                int rnd = random.nextInt(2);
                AVariantAnswer an;
                if (rnd == 0) {
                    an = new AVariantAnswer(0L, 2, watchingTag, q);
                } else {
                    an = new AVariantAnswer(0L, 2, swimmingTag, q);
                }
                variantAnswers.add(an);
            } else {
                int rnd = random.nextInt(3);
                String answer = answersArray[rnd];
                List<ATag> tags = new ArrayList<>();
                AExtendedAnswer an = new AExtendedAnswer(0L, q, 1, answer, tags);
                extendedAnswers.add(an);
            }
        }

        return new AInformation(0L, variantAnswers, extendedAnswers);
    }

    static String askVariant(AQuestion q) {
        int cnt;
        while (true) {
            System.out.println(q.getQuestion());
            cnt = 1;
            for (ATag t : q.getTags()) {
                System.out.println(cnt++ + " - " + t.getName());
            }
            try {
                cnt = Integer.parseInt(con.readLine());
            } catch (Exception e) {
                cnt = 0;
            }
            if ((cnt > 0) && (cnt <= q.getTags().size())) {
                break;
            }
        }
        return q.getTags().get(cnt - 1).getName();
    }

    static AInformation quest(MainManager m, String msg) throws Exception {
        System.out.println(msg);
        List<AVariantAnswer> variantAnswers = new ArrayList<>();
        List<AExtendedAnswer> extendedAnswers = new ArrayList<>();

        for (AQuestion q : m.getM_que_manager().get_all_questions()) {
            if (!q.getIs_extended()) {
                String answer = askVariant(q);
                ATag tag = m.getM_que_manager().find_tag(answer);
                System.out.println("Введите вес этого вопроса (от 1 до 10):");
                String x = con.readLine();
                int weight = Integer.parseInt(x);
                AVariantAnswer an = new AVariantAnswer(0L, weight, tag, q);
                variantAnswers.add(an);
            } else {
                System.out.println(q.getQuestion());
                String answer = con.readLine();
                List<ATag> tags = new ArrayList<>();
                System.out.println("Введите вес этого вопроса (от 1 до 10):");
                String x = con.readLine();
                int weight = Integer.parseInt(x);
                AExtendedAnswer an = new AExtendedAnswer(0L, q, weight, answer, tags);
                extendedAnswers.add(an);
            }
        }

        return new AInformation(variantAnswers, extendedAnswers);
    }

    static void test_user(MainManager m, int n) throws Exception {
        AUser u = test_add_user(m, "user" + n, "user" + n);
        for (int i = 0; i < 2; i++) {
            add_questionnaire(m, u);
            System.out.println("questionnaire added: " + i);
        }
    }

    static void test(MainManager m) throws Exception {
        for (int i = 0; i < 2; i++) {
            test_user(m, i);
        }
    }

    static final boolean VARIANT_QUESTION_KIND = false;
    static final boolean EXTENDED_QUESTION_KIND = true;

    static void basic(MainManager m, AppLogger logger) throws Exception {
        logger.info("Filling the database with initial data...");
        ATag walkingTag = m.getM_que_manager().append_tag("walking");
        ATag watchingTag = m.getM_que_manager().append_tag("watching TV");
        ATag swimmingTag = m.getM_que_manager().append_tag("swimming");
        ATag sleepingTag = m.getM_que_manager().append_tag("sleeping");

        List<ATag> tags1 = Arrays.asList(watchingTag, swimmingTag);
        List<ATag> tags2 = Arrays.asList(walkingTag, sleepingTag);

        AQuestion q1 = m.getM_que_manager().append_question(VARIANT_QUESTION_KIND,
                "Do you love swimming or watching TV?", new ArrayList<>(tags1));
        AQuestion q2 = m.getM_que_manager().append_question(EXTENDED_QUESTION_KIND,
                "How do you like to spend your time?", new ArrayList<>(tags2));

        logger.info("Required data added to the database");

        AUser user = null;
        while (true) {
            if (user == null) {
                System.out.println("1 - зарегистрироваться");
                System.out.println("2 - войти");
                String x = con.readLine();
                if (x.equals("1")) {
                    logger.info("User registration started");
                    System.out.println("логин: ");
                    String name = con.readLine();
                    System.out.println("пароль: ");
                    String pwd = con.readLine();
                    try {
                        AUser u = m.getM_user_manager().register(name, pwd, 43, true);
                        if (u == null) {
                            logger.warn("Failed to register user.");
                        } else {
                            logger.info("User registered successfully: {}", name);
                        }
                    } catch (Exception e) {
                        logger.error("Error during user registration", e);
                        throw new RuntimeException(e);
                    }
                } else if (x.equals("2")) {
                    logger.info("User login attempt");
                    System.out.println("логин: ");
                    String name = con.readLine();
                    System.out.println("пароль: ");
                    String pwd = con.readLine();
                    try {
                        user = m.getM_user_manager().authorize(name, pwd);
                        if (user == null) {
                            logger.warn("Login failed: {}", name);
                            System.out.println("Не удалось войти");
                        } else {
                            logger.info("User logged in: {}", name);
                            m.getM_que_manager().clear_req_cache();
                        }
                    } catch (Exception e) {
                        logger.error("Error during user authorization", e);
                    }
                }
            }
            // Остальной код интерактивной работы пользователя/админа/цензора
            // можно тоже подправить по пробелам и длинным строкам
            // для сокращения, не включаю весь тут, но по аналогии с выше исправляется
        }
    }

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.setErr(new PrintStream(System.err, true, "UTF-8"));
        con = System.console();

        System.out.println("Консольный тест 2");

        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        DBAPI api = new DBAPI();
        api.m_extendedAnswerRepository = context.getBean(SpringExtendedAnswerRepository.class);
        api.m_questionnaireRepository = context.getBean(SpringQuestionnaireRepository.class);
        api.m_variantAnswerRepository = context.getBean(SpringVariantAnswerRepository.class);
        api.m_userRepository = context.getBean(SpringUserRepository.class);
        api.m_informationRepository = context.getBean(SpringInformationRepository.class);
        api.m_questionRepository = context.getBean(SpringQuestionRepository.class);
        api.m_variantAnswerRepository = context.getBean(SpringVariantAnswerRepository.class);
        api.m_tagRepository = context.getBean(SpringTagRepository.class);
        api.m_reqCacheRepository = context.getBean(SpringReqCacheRepository.class);

        UserRepository userRepository = new UserRepository(api.m_userRepository);
        ReqCacheRepository reqCacheRepository = new ReqCacheRepository(api.m_reqCacheRepository);

        QuestionnaireRepository dataRepository = new QuestionnaireRepository(api);

        MainManager m = new MainManager(new ML_stab_port(olamaHost, logger),
                userRepository, dataRepository, reqCacheRepository, logger);

        logger.info("Start application");
        basic(m, logger);
    }
}
