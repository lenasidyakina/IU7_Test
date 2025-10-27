package ru.bmstu.iu7;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.impl.*;
import ru.bmstu.iu7.src.MainManager;
import ru.bmstu.iu7.src.controllers.ReqCacheController;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
@Component
public class Main {

    public static DBAPI api;
    public static UserRepository userRepository;
    public static ReqCacheRepository reqCacheRepository;
    public static QuestionnaireRepository dataRepository;
    public static MainManager mainManager;
    public static ConfigurableApplicationContext context;
    static AppLogger logger;
    public static AUser currentUser;

    @Value("${olama-host}")
    private String wiredOlamaHost;

    private static String olamaHost;

    @PostConstruct
    public void init() {
        olamaHost = wiredOlamaHost;
        logger = new AppLoggerImpl("AppLogger");
        ReqCacheController.m_list = new ArrayList<AQuestionnaire>();
    }

    public static void main(String[] args) throws Exception {
        context = SpringApplication.run(Main.class, args);

        api = new DBAPI();
        api.m_extendedAnswerRepository = context.getBean(SpringExtendedAnswerRepository.class);
        api.m_questionnaireRepository = context.getBean(SpringQuestionnaireRepository.class);
        api.m_variantAnswerRepository = context.getBean(SpringVariantAnswerRepository.class);
        api.m_userRepository = context.getBean(SpringUserRepository.class);
        api.m_informationRepository = context.getBean(SpringInformationRepository.class);
        api.m_questionRepository = context.getBean(SpringQuestionRepository.class);
        api.m_tagRepository = context.getBean(SpringTagRepository.class);
        api.m_reqCacheRepository = context.getBean(SpringReqCacheRepository.class);

        userRepository = new UserRepository(api.m_userRepository);
        reqCacheRepository = new ReqCacheRepository(api.m_reqCacheRepository);
        dataRepository = new QuestionnaireRepository(api);

        mainManager = new MainManager(
                new ML_port(olamaHost),
                userRepository,
                dataRepository,
                reqCacheRepository,
                logger
        );
        currentUser = null;
        logger.info("Filling the database with initial data...");
        ATag walking_tag = mainManager.getM_que_manager().append_tag("walking");
        ATag watching_tag = mainManager.getM_que_manager().append_tag("watching TV");
        ATag swimming_tag = mainManager.getM_que_manager().append_tag("swimming");
        ATag sleeping_tag = mainManager.getM_que_manager().append_tag("sleeping");

        AQuestion q_1 = mainManager.getM_que_manager().append_question(false,
                "Do you love swimming or watching TV?", new ArrayList<>(Arrays.asList(watching_tag, swimming_tag)));
        AQuestion q_2 = mainManager.getM_que_manager().append_question(true,
                "How do you like to spend your time?", new ArrayList<>(Arrays.asList(walking_tag, sleeping_tag)));

        logger.info("Required data added to the database");
    }
}
