package ru.bmstu.iu7;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
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
    private  String wiredOlamaHost;

    static private String olamaHost;
    static AppLogger logger;

    @PostConstruct
    public void init() {
        olamaHost = wiredOlamaHost;
        logger = new AppLoggerImpl("AppLogger");
        ReqCacheController.m_list = new ArrayList<AQuestionnaire>();
    }

    @Value("${ml.api.url}")
    private static String mlApiUrl;

    @Value("${ml.api.content-type}")

    static Console con;


    //private static final Logger logger = LoggerFactory.getLogger(Main.class);



    static AUser test_add_user(MainManager  m, String name, String pwd) throws Exception {
        return m.getM_user_manager().register(name, pwd, 43, true);
    }

    static ATag test_add_tag(MainManager  m, String tag) throws Exception {
        return m.getM_que_manager().append_tag("tag");
    }

    static AQuestion test_add_variant_question(MainManager  m, String text, List<ATag> tags) throws Exception {
        return m.getM_que_manager().append_question(VARIANT_QUESTION_KIND, text, tags);
    }

    static AQuestion test_add_extended_question(MainManager  m, String text) throws Exception {
        return m.getM_que_manager().append_question(EXTENDED_QUESTION_KIND, text, new ArrayList<ATag>(){});
    }

    static AQuestionnaire add_questionnaire(MainManager  m, AUser user) throws Exception {
        AInformation info1 = emulate_information(m);
        AInformation info2 = emulate_information(m);
        return m.getM_que_manager().create(user, info1, info2);
    }

    static AInformation emulate_information(MainManager  m) throws Exception
    {
        ATag walking_tag = m.getM_que_manager().append_tag("walking");
        ATag watching_tag = m.getM_que_manager().append_tag("watching TV");
        ATag swimming_tag = m.getM_que_manager().append_tag("swimming");
        ATag sleeping_tag = m.getM_que_manager().append_tag("sleeping");

        AQuestion q_1 = m.getM_que_manager().append_question(VARIANT_QUESTION_KIND,
                "Do you love swimming or watching TV?", new ArrayList<>(Arrays.asList(watching_tag, swimming_tag)));
        AQuestion q_2 =  m.getM_que_manager().append_question(EXTENDED_QUESTION_KIND,
                "How do you like to spend your time?", new ArrayList<>(Arrays.asList(walking_tag, sleeping_tag)));

        String array[] = new String[] {
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
                    an = new AVariantAnswer(0L,2, watching_tag, q);
                }
                else {
                    an = new AVariantAnswer(0L,2, swimming_tag, q);
                }
                variantAnswers.add(an);
            } else {
                int rnd = random.nextInt(3);
                System.out.println(q.getQuestion());
                String answer = array[rnd];
                List<ATag> tags = new ArrayList<>();
                AExtendedAnswer an = new AExtendedAnswer(0L, q, 1, answer, tags);
                extendedAnswers.add(an);
            }
        }
        return new AInformation(0L, variantAnswers, extendedAnswers);
    }

    static String askVariant(AQuestion q)
    {
        int cnt;
        while(true) {
            System.out.println(q.getQuestion());
            cnt = 1;
            for (ATag t : q.getTags()) {
                System.out.println(String.valueOf(cnt++) + " - " + t.getName());
            }
            try {
                cnt = Integer.parseInt(con.readLine());
            }
            catch (Exception e) {
                cnt = 0;
            }
            if ((cnt > 0) && (cnt <= q.getTags().size()))
                break;
        };
        return q.getTags().get(cnt - 1).getName();
    }


    static AInformation quest(MainManager  m, String msg) throws Exception
    {
        System.out.println(msg);
        List<AVariantAnswer> variantAnswers = new ArrayList<>();
        List<AExtendedAnswer> extendedAnswers = new ArrayList<>();
        for (AQuestion q : m.getM_que_manager().get_all_questions()) {
            if (!q.getIs_extended()) {
                // !kind = variant
                String answer = askVariant(q);
                ATag tag = m.getM_que_manager().find_tag(answer);
                System.out.println("Введите вес этого вопроса (от 1 до 10):");
                String x = con.readLine();
                int weight = Integer.parseInt(x);
                AVariantAnswer an = new AVariantAnswer(0L,weight, tag, q);
                variantAnswers.add(an);
            } else {
                // kind = extended
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

    static void test_user(MainManager  m, int n) throws Exception {
        AUser u = test_add_user(m, "user" + String.valueOf(n), "user" + String.valueOf(n));
        for (int i = 0; i < 2; i++) {
            add_questionnaire(m, u);
            System.out.println("questionnarie added: " + i);
        }
    }
    static void test(MainManager  m) throws Exception {
        for (int i = 0; i < 2; i++) {
            test_user(m, i);
        }
    }

    static final boolean VARIANT_QUESTION_KIND = false;
    static final boolean EXTENDED_QUESTION_KIND = true;


    static void basic(MainManager m, AppLogger logger) throws Exception {
        logger.info("Filling the database with initial data...");
        ATag walking_tag = m.getM_que_manager().append_tag("walking");
        ATag watching_tag = m.getM_que_manager().append_tag("watching TV");
        ATag swimming_tag = m.getM_que_manager().append_tag("swimming");
        ATag sleeping_tag = m.getM_que_manager().append_tag("sleeping");

        AQuestion q_1 = m.getM_que_manager().append_question(VARIANT_QUESTION_KIND,
                "Do you love swimming or watching TV?", new ArrayList<>(Arrays.asList(watching_tag, swimming_tag)));
        AQuestion q_2 = m.getM_que_manager().append_question(EXTENDED_QUESTION_KIND,
                "How do you like to spend your time?", new ArrayList<>(Arrays.asList(walking_tag, sleeping_tag)));

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
            } else if (user.getRole().equals("user")) {
                System.out.println("1 - создать анкету");
                System.out.println("2 - выйти");
                System.out.println("3 - получить список потенциальных друзей");
                System.out.println("4 - вывести избранное");
                System.out.println("5 - вывести чёрный список");

                String x = con.readLine();
                if (x.equals("2")) {
                    logger.info("User {} logged out", user.getName());
                    user = null;
                } else if (x.equals("1")) {
                    logger.info("Creating a new questionnaire for user {}", user.getName());
                    AInformation info1 = quest(m, "Часть 1. Ответь на вопросы от своего лица.");
                    AInformation info2 = quest(m, "Часть 2. Ответь на вопросы от лица потенциального друга.");
                    AQuestionnaire questionnaire = m.getM_que_manager().create(user, info1, info2);
                    logger.info("Questionnaire successfully created for user {}", user.getName());
                    System.out.println("Анкета успешно создана");
                } else if (x.equals("3")) {
                    logger.info("Requesting potential friends list for user {}", user.getName());
                    AQuestionnaire activeQuest = null;
                    List<AQuestionnaire> list = m.getM_que_manager().get_user_questionnaies(user.getId());
                    if (list.size() > 0) {
                        if (list.size() == 1)
                            activeQuest = list.get(0);
                        else {
                            System.out.println("Выберете анкету:");
                            int ctx = 1;
                            for (AQuestionnaire q : list) {
                                System.out.println(String.valueOf(ctx++));
                            }
                            try {
                                String idx = con.readLine();
                                int n = Integer.parseInt(idx);
                                activeQuest = list.get(n - 1);
                            } catch (Exception e) {
                                logger.error("Error while selecting questionnaire", e);
                                activeQuest = null;
                            }
                        }
                        if (activeQuest != null) {
                            ReqCacheController.m_list.clear();
                            ReqCacheController.running = false;
                            m.getM_que_manager().set_active_questionnaire(activeQuest);
                            System.out.println("Ваши потенциальные друзья:");
                            List<AQuestionnaire> qs = m.getM_rec_manager().get_friends();
                            List<Long> help_list = new ArrayList<>();
                            for (AQuestionnaire q : qs) {
                                if ((!activeQuest.getUser().getId().equals(q.getUser().getId())) && (!help_list.contains(q.getId()))
                                        && (!activeQuest.getBlackList().contains(q)) && (!activeQuest.getFavList().contains(q))) {
                                    System.out.println(q.getUser().getName() + " " + q.getId());
                                    help_list.add(q.getId());
                                }
                            }

                            System.out.println("1 - добавить в чёрный список");
                            System.out.println("2 - добавить в избранное");
                            System.out.println("3 - выход");
                            System.out.println("4 - удалить из избранного");
                            System.out.println("5 - удалить из чёрного списка");
                            String y = con.readLine();
                            if (y.equals("3")) {
                                continue;
                            }
                            if (y.equals("1")) {
                                logger.info("Adding to blacklist by user {}", user.getName());
                                System.out.println("Выберете номер анкеты:");
                                help_list = new ArrayList<>();
                                for (AQuestionnaire q : qs) {
                                    if ((!activeQuest.getUser().getId().equals(q.getUser().getId())) && (!help_list.contains(q.getId()))
                                            && (!activeQuest.getBlackList().contains(q)) && (!activeQuest.getFavList().contains(q))) {
                                        System.out.println(q.getUser().getName() + " " + q.getId());
                                        help_list.add(q.getId());
                                    }
                                }
                                String z = con.readLine();
                                Long number = 0L;
                                try {
                                    number = Long.parseLong(z);
                                } catch (NumberFormatException e) {
                                    logger.error("Invalid number input", e);
                                }
                                AQuestionnaire quest = m.getM_que_manager().findQuestionnaire(number);
                                m.getM_que_manager().add_black(quest);
                            } else if (y.equals("2")) {
                                logger.info("Adding to favorites by user {}", user.getName());
                                System.out.println("Выберете номер анкеты:");
                                help_list = new ArrayList<>();
                                for (AQuestionnaire q : qs) {
                                    if ((!activeQuest.getUser().getId().equals(q.getUser().getId())) && (!help_list.contains(q.getId()))
                                            && (!activeQuest.getBlackList().contains(q)) && (!activeQuest.getFavList().contains(q))) {
                                        System.out.println(q.getUser().getName() + " " + q.getId());
                                        help_list.add(q.getId());
                                    }
                                }
                                String z = con.readLine();
                                Long number = 0L;
                                try {
                                    number = Long.parseLong(z);
                                } catch (NumberFormatException e) {
                                    logger.error("Invalid number input", e);
                                }
                                AQuestionnaire quest = m.getM_que_manager().findQuestionnaire(number);
                                m.getM_que_manager().add_fav(quest);
                            } else if (y.equals("4")) {
                                logger.info("Removing from favorites by user {}", user.getName());
                                System.out.println("Выберете номер анкеты:");
                                if (activeQuest != null) {
                                    List<AQuestionnaire> fav_list = activeQuest.getFavList();
                                    for (AQuestionnaire q : fav_list) {
                                        System.out.println(q.getUser().getName() + " " + q.getId());
                                    }
                                }
                                String z = con.readLine();
                                Long number = 0L;
                                try {
                                    number = Long.parseLong(z);
                                } catch (NumberFormatException e) {
                                    logger.error("Invalid number input", e);
                                }
                                AQuestionnaire quest = m.getM_que_manager().findQuestionnaire(number);
                                m.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(quest));
                                m.getM_que_manager().del_fav(quest);
                            } else if (y.equals("5")) {
                                logger.info("Removing from blacklist by user {}", user.getName());
                                System.out.println("Выберете номер анкеты:");
                                if (activeQuest != null) {
                                    List<AQuestionnaire> black_list = activeQuest.getBlackList();
                                    for (AQuestionnaire q : black_list) {
                                        System.out.println(q.getUser().getName() + " " + q.getId());
                                    }
                                }
                                String z = con.readLine();
                                Long number = 0L;
                                try {
                                    number = Long.parseLong(z);
                                } catch (NumberFormatException e) {
                                    logger.error("Invalid number input", e);
                                }
                                AQuestionnaire quest = m.getM_que_manager().findQuestionnaire(number);
                                m.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(quest));
                                m.getM_que_manager().del_black(quest);
                            }
                        }
                    }
                } else if (x.equals("4")) {
                    logger.info("Favorites request by user {}", user.getName());
                    AQuestionnaire activeQuest = null;
                    List<AQuestionnaire> list = m.getM_que_manager().get_user_questionnaies(user.getId());
                    if (list.size() > 0) {
                        if (list.size() == 1)
                            activeQuest = list.get(0);
                        else {
                            System.out.println("Выберете номер анкеты:");
                            int ctx = 1;
                            for (AQuestionnaire q : list) {
                                System.out.println(String.valueOf(ctx++));
                            }
                            try {
                                String idx = con.readLine();
                                int n = Integer.parseInt(idx);
                                activeQuest = list.get(n - 1);
                            } catch (Exception e) {
                                logger.error("Error while selecting questionnaire", e);
                                activeQuest = null;
                            }
                        }
                        if (activeQuest != null) {
                            List<AQuestionnaire> fav_list = activeQuest.getFavList();
                            for (AQuestionnaire q : fav_list) {
                                System.out.println(q.getUser().getName() + " " + q.getId());
                            }
                        }
                    }
                } else if (x.equals("5")) {
                    logger.info("Blacklist request by user {}", user.getName());
                    AQuestionnaire activeQuest = null;
                    List<AQuestionnaire> list = m.getM_que_manager().get_user_questionnaies(user.getId());
                    if (list.size() > 0) {
                        if (list.size() == 1)
                            activeQuest = list.get(0);
                        else {
                            System.out.println("Выберете номер анкеты:");
                            int ctx = 1;
                            for (AQuestionnaire q : list) {
                                System.out.println(String.valueOf(ctx++));
                            }
                            try {
                                String idx = con.readLine();
                                int n = Integer.parseInt(idx);
                                activeQuest = list.get(n - 1);
                            } catch (Exception e) {
                                logger.error("Error while selecting questionnaire", e);
                                activeQuest = null;
                            }
                        }
                        if (activeQuest != null) {
                            List<AQuestionnaire> fav_list = activeQuest.getBlackList();
                            for (AQuestionnaire q : fav_list) {
                                System.out.println(q.getUser().getName() + " " + q.getId());
                            }
                        }
                    }
                }
            } else if (user.getRole().equals("censor")) {
                logger.info("Censor {} logged in", user.getName());
                System.out.println("1 - посмотреть все анкеты");
                System.out.println("2 - пометить анкету цензурированной");
                System.out.println("3 - выход");
                String y = con.readLine();
                if (y.equals("3")) {
                    logger.info("Censor {} logged out", user.getName());
                    user = null;
                } else if (y.equals("1")) {
                    logger.info("Censor {} requested all questionnaires", user.getName());
                    List<AQuestionnaire> quest = m.getM_que_manager().get_all_questionnaires();
                    for (AQuestionnaire q : quest) {
                        System.out.println(q.getUser().getName() + " " + q.getId() + " cens:" + q.isCensored());
                    }
                } else if (y.equals("2")) {
                    logger.info("Censor {} marks questionnaire as censored", user.getName());
                    System.out.println("Choose questionnaire number:");
                    int ctx = 1;
                    List<AQuestionnaire> quest = m.getM_que_manager().get_all_questionnaires();
                    for (AQuestionnaire q : quest) {
                        if (!q.isCensored()) {
                            System.out.println(q.getUser().getName() + " " + q.getId());
                        }
                    }
                    String z = con.readLine();
                    Long number = 0L;
                    try {
                        number = Long.parseLong(z);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number input", e);
                    }
                    AQuestionnaire quest_1 = m.getM_que_manager().findQuestionnaire(number);
                    m.getM_que_manager().censor(quest_1);
                    logger.info("Questionnaire {} marked as censored", number);
                    System.out.println("Questionnaire marked as censored.");
                }
            } else if (user.getRole().equals("admin")) {
                logger.info("Administrator {} logged in", user.getName());
                System.out.println("1 - посмотреть все анкеты");
                System.out.println("2 - удалить анкету");
                System.out.println("3 - выход");
                String y = con.readLine();
                if (y.equals("3")) {
                    logger.info("Administrator {} logged out", user.getName());
                    user = null;
                }
                if (y.equals("1")) {
                    logger.info("Administrator {} requested all questionnaires", user.getName());
                    List<AQuestionnaire> quest = m.getM_que_manager().get_all_questionnaires();
                    for (AQuestionnaire q : quest) {
                        System.out.println(q.getUser().getName() + " " + q.getId() + " cens:" + q.isCensored());
                    }
                } else if (y.equals("2")) {
                    logger.info("Administrator {} deletes a questionnaire", user.getName());
                    System.out.println("Choose questionnaire number:");
                    int ctx = 1;
                    List<AQuestionnaire> quest = m.getM_que_manager().get_all_questionnaires();
                    for (AQuestionnaire q : quest) {
                        if (!q.isCensored()) {
                            System.out.println(q.getUser().getName() + " " + q.getId());
                        }
                    }
                    String z = con.readLine();
                    Long number = 0L;
                    try {
                        number = Long.parseLong(z);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number input", e);
                    }
                    AQuestionnaire quest_1 = m.getM_que_manager().findQuestionnaire(number);
                    m.getM_que_manager().delete_questionnaire(quest_1);
                    logger.info("Questionnaire {} deleted by administrator", number);
                }
            }
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
        //AppLogger logger = new AppLoggerImpl("AppLogger");

        MainManager m = new MainManager(new ML_stab_port(olamaHost, logger),userRepository, dataRepository,
                reqCacheRepository, logger);


        logger.info("Start application");
        //test(m);
        basic(m, logger);

    }

}

