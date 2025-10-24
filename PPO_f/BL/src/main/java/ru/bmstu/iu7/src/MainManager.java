package ru.bmstu.iu7.src;

import ru.bmstu.iu7.API.*;
import ru.bmstu.iu7.src.controllers.QuestionnaireController;
import ru.bmstu.iu7.src.managers.QuestionnaireManager;
import ru.bmstu.iu7.src.managers.RecManager;
import ru.bmstu.iu7.src.managers.UserManager;

public class MainManager {
    public MainManager(IML_port imlPort, IUserRepository userRepository,
                       IQuestionnaireRepository questRepository,
                       IReqCacheRepository cacheRepository, AppLogger logger) throws Exception
    {
        QuestionnaireController controller = new QuestionnaireController(imlPort, questRepository, cacheRepository);
        m_user_manager = new UserManager(userRepository, logger);
        m_que_manager = new QuestionnaireManager(controller, logger);
        m_rec_manager = new RecManager(controller, logger);
    }

    public UserManager getM_user_manager() {
        return m_user_manager;
    }

    public QuestionnaireManager getM_que_manager() {
        return m_que_manager;
    }

    public RecManager getM_rec_manager() {
        return m_rec_manager;
    }

    private final UserManager m_user_manager;
    private final QuestionnaireManager m_que_manager;
    private final RecManager m_rec_manager;

}
