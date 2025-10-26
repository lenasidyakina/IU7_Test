package ru.bmstu.iu7;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.model.AQuestionnaire;
import ru.bmstu.iu7.API.model.AUser;
import ru.bmstu.iu7.impl.*;
import ru.bmstu.iu7.src.MainManager;
import ru.bmstu.iu7.src.controllers.ReqCacheController;

import java.util.ArrayList;

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
    }
}
