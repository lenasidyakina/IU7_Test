package ru.bmstu.iu7.impl;

import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.impl.model.*;
import ru.bmstu.iu7.impl.model.Questionnaire;
import ru.bmstu.iu7.impl.model.User;

import java.util.ArrayList;
import java.util.List;

public class ModelFactory {
    public static ExtendedAnswer makeExtendedAnswer(Long id, Question question,
                                              int weight, String answer, List<Tag> tags) {
        ExtendedAnswer ans = new ExtendedAnswer();
        ans.setQuestion(question);
        ans.setWeight(weight);
        ans.setAnswer(answer);
        ans.setTags(tags);
        return ans;
    }


    public static Information makeInformation(Long id, List<VariantAnswer> variantAnswers,
                                        List<ExtendedAnswer> extendedAnswers) {
        Information inf = new Information();
        inf.setId(id);
        inf.setVariantAnswers(variantAnswers);
        inf.setExtendedAnswers(extendedAnswers);
        return inf;
    }


    public static Question makeQuestion(Long id, String question, List<Tag> tags, boolean kind) {
        Question q = new Question();
        q.setId(id);
        q.setQuestion(question);
        q.setTags(tags);
        q.setIs_extended(kind);
        return q;
    }

    public static Questionnaire makeQuestionnaire(Long id, User user, Information information, Information searchInformation,
                                                   List<Questionnaire> blacklist, List<Questionnaire> favList, boolean censored)
    {
        Questionnaire q = new Questionnaire();
        q.setId(id);
        q.setBlackList( blacklist);
        q.setFavList( favList);
        q.setUser( user );
        q.setInformation( information );
        q.setSearchInformation( searchInformation );
        q.setCensored(censored);
        return q;
    }

    public static Tag makeTag(Long id, String name) {
        Tag t = new Tag();
        t.setId(id);
        t.setName(name);
        return t;
    }

    public static ReqCache makeReqCache(Long id, double Factor,
                                   Questionnaire questionnaire1, Questionnaire questionnaire2) {
        ReqCache t = new ReqCache();
        t.setId(id);
        t.setFactor(Factor);
        t.setQuestionnaire1(questionnaire1);
        t.setQuestionnaire2(questionnaire2);
        return t;
    }

    public static User makeUser(Long id, String name, String password, int age, boolean gender) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setPassword(password);
        user.setAge(age);
        user.setGender(gender);
        return user;
    }

    public static VariantAnswer makeVariantAnswer(Long id, int weight, Tag tag, Question question){
        VariantAnswer answer = new VariantAnswer();
        answer.setId(id);
        answer.setWeight(weight);
        answer.setTag(tag);
        answer.setQuestion(question);
        return answer;
    }


    public static ATag Tag2ATag(Tag tag)
    {
        if (tag == null)
            return null;
        return new ATag(tag.getId(), tag.getName());
    }

    public static Tag ATag2Tag(ATag tag)
    {
        if (tag == null)
            return null;
        return makeTag(tag.getId(), tag.getName());
    }

    public static List<ATag> Tags2ATags(List<Tag> tags)
    {
        List<ATag> ans = new ArrayList<>();
        for (Tag tag : tags) {
            ans.add(Tag2ATag(tag));
        }
        return ans;
    }

    public static List<AUser> Users2AUsers(List<User> users)
    {
        List<AUser> ans = new ArrayList<>();
        for (User user : users) {
            ans.add(User2AUser(user));
        }
        return ans;
    }

    public static AReqCache ReqCache2AReqCache(ReqCache reqCache)
    {
        if (reqCache == null)
            return null;
        return new AReqCache(reqCache.getId(), reqCache.getFactor(),
             Questionnaire2AQuestionnaire(reqCache.getQuestionnaire1()),
                Questionnaire2AQuestionnaire(reqCache.getQuestionnaire2()));
    }

    public static ReqCache AReqCache2ReqCache(AReqCache reqCache)
    {
        if (reqCache == null)
            return null;
        return makeReqCache(reqCache.getId(), reqCache.getFactor(),
                AQuestionnaire2Questionnaire(reqCache.getQuestionnaire1()),
                AQuestionnaire2Questionnaire(reqCache.getQuestionnaire1()));
    }

    public static List<AReqCache> ReqCaches2AReqCaches(List<ReqCache> reqCaches)
    {
        List<AReqCache> ans = new ArrayList<>();
        for (ReqCache reqCache : reqCaches) {
            ans.add(ReqCache2AReqCache(reqCache));
        }
        return ans;
    }




    public static List<AQuestion> Questions2AQuestions(List<Question> questions)
    {
        List<AQuestion> ans = new ArrayList<>();
        for (Question question : questions) {
            ans.add(Question2AQuestion(question));
        }
        return ans;
    }

    public static List<Tag> ATags2Tags(List<ATag> tags)
    {
        List<Tag> ans = new ArrayList<>();
        for (ATag tag : tags) {
            ans.add(ATag2Tag(tag));
        }
        return ans;
    }

    public static Question AQuestion2Question(AQuestion question)
    {
        return makeQuestion(question.getId(), question.getQuestion(),
                ATags2Tags(question.getTags()), question.getIs_extended());
    }

    public static AQuestion Question2AQuestion(Question question)
    {
        if (question == null)
            return null;
        return new AQuestion(question.getId(), question.getQuestion(),
                Tags2ATags(question.getTags()), question.getIs_extended());
    }

    public static VariantAnswer AVariantAnswer2VariantAnswer(AVariantAnswer answer)
    {
        if (answer == null)
            return null;
        return makeVariantAnswer(answer.getId(), answer.getWeight(),
                ATag2Tag(answer.getTag()), AQuestion2Question(answer.getQuestion()));
    }

    public static VariantAnswer AVariantAnswer2VariantAnswerNew(AVariantAnswer answer)
    {
        if (answer == null)
            return null;
        return makeVariantAnswer(null, answer.getWeight(),
                ATag2Tag(answer.getTag()), AQuestion2Question(answer.getQuestion()));
    }

    public static AVariantAnswer VariantAnswer2AVariantAnswer(VariantAnswer answer)
    {
        if (answer == null)
            return null;
        return new AVariantAnswer(answer.getId(), answer.getWeight(),
                Tag2ATag(answer.getTag()), Question2AQuestion(answer.getQuestion()));
    }

    public static ExtendedAnswer AExtendedAnswer2ExtendedAnswer(AExtendedAnswer answer)
    {
        if (answer == null)
            return null;
        return makeExtendedAnswer(answer.getId(), AQuestion2Question(answer.getQuestion()), answer.getWeight(),
                answer.getAnswer(), ATags2Tags(answer.getTags()));
    }

    public static ExtendedAnswer AExtendedAnswer2ExtendedAnswerNew(AExtendedAnswer answer)
    {
        if (answer == null)
            return null;
        return makeExtendedAnswer(null, AQuestion2Question(answer.getQuestion()), answer.getWeight(),
                answer.getAnswer(), ATags2Tags(answer.getTags()));
    }

    public static AExtendedAnswer ExtendedAnswer2AExtendedAnswer(ExtendedAnswer answer)
    {
        if (answer == null)
            return null;
        return new AExtendedAnswer(answer.getId(), Question2AQuestion(answer.getQuestion()),
                answer.getWeight(), answer.getAnswer(), Tags2ATags(answer.getTags()));
    }

    public static List<AExtendedAnswer> ExtendedAnswers2AExtendedAnswers(List<ExtendedAnswer> answers)
    {
        List<AExtendedAnswer> ans = new ArrayList<>();
        for (ExtendedAnswer answer : answers) {
            ans.add(ExtendedAnswer2AExtendedAnswer(answer));
        }
        return ans;
    }

    public static List<ExtendedAnswer> AExtendedAnswers2ExtendedAnswers(List<AExtendedAnswer> answers)
    {
        List<ExtendedAnswer> ans = new ArrayList<>();
        for (AExtendedAnswer answer : answers) {
            ans.add(AExtendedAnswer2ExtendedAnswer(answer));
        }
        return ans;
    }

    public static List<ExtendedAnswer> AExtendedAnswers2ExtendedAnswersNew(List<AExtendedAnswer> answers)
    {
        List<ExtendedAnswer> ans = new ArrayList<>();
        for (AExtendedAnswer answer : answers) {
            ans.add(AExtendedAnswer2ExtendedAnswerNew(answer));
        }
        return ans;
    }

    public static List<AVariantAnswer> VariantAnswers2AVariantAnswers(List<VariantAnswer> answers)
    {
        List<AVariantAnswer> ans = new ArrayList<>();
        for (VariantAnswer answer : answers) {
            ans.add(VariantAnswer2AVariantAnswer(answer));
        }
        return ans;
    }

    public static List<VariantAnswer> AVariantAnswers2VariantAnswers(List<AVariantAnswer> answers)
    {
        List<VariantAnswer> ans = new ArrayList<>();
        for (AVariantAnswer answer : answers) {
            ans.add(AVariantAnswer2VariantAnswer(answer));
        }
        return ans;
    }

    public static List<VariantAnswer> AVariantAnswers2VariantAnswersNew(List<AVariantAnswer> answers)
    {
        List<VariantAnswer> ans = new ArrayList<>();
        for (AVariantAnswer answer : answers) {
            ans.add(AVariantAnswer2VariantAnswerNew(answer));
        }
        return ans;
    }

    public static AInformation Information2AInformation(Information info)
    {
        if (info == null)
            return null;
        return new AInformation(info.getId(),
                VariantAnswers2AVariantAnswers(info.getVariantAnswers()),
                ExtendedAnswers2AExtendedAnswers(info.getExtendedAnswers()));
    }

    public static Information AInformation2Information(AInformation info)
    {
        if (info == null)
            return null;
        return makeInformation(info.getId(),
                AVariantAnswers2VariantAnswers(info.getVariantAnswers()),
                AExtendedAnswers2ExtendedAnswers(info.getExtendedAnswers()));
    }

    public static Information AInformation2InformationNew(AInformation info)
    {
        if (info == null)
            return null;
        return makeInformation( null,
                AVariantAnswers2VariantAnswersNew(info.getVariantAnswers()),
                AExtendedAnswers2ExtendedAnswersNew(info.getExtendedAnswers()));
    }

    // do not pass black/faw list from DB to BL
    public static AQuestionnaire Questionnaire2AQuestionnaireShort(Questionnaire questionnaire)
    {
        if (questionnaire == null)
            return null;
        return new AQuestionnaire(questionnaire.getId(), User2AUser(questionnaire.getUser()),
                Information2AInformation(questionnaire.getInformation()),
                Information2AInformation(questionnaire.getSearchInformation()),
                new ArrayList<AQuestionnaire>(),
                new ArrayList<AQuestionnaire>(), questionnaire.isCensored());
    }


    // do not pass black/faw list  from DB to BL
    public static List<AQuestionnaire> Questionnaires2AQuestionnairesShort(List<Questionnaire> questionnaires)
    {
        List<AQuestionnaire> ans = new ArrayList<>();
        for (Questionnaire questionnaire : questionnaires) {
            ans.add(Questionnaire2AQuestionnaireShort(questionnaire));
        }
        return ans;
    }

    public static List<AQuestionnaire> Questionnaires2AQuestionnaires(List<Questionnaire> questionnaires)
    {
        List<AQuestionnaire> ans = new ArrayList<>();
        for (Questionnaire questionnaire : questionnaires) {
            ans.add(Questionnaire2AQuestionnaire(questionnaire));
        }
        return ans;
    }

    public static List<Questionnaire> AQuestionnaires2Questionnaires(List<AQuestionnaire> questionnaires)
    {
        List<Questionnaire> ans = new ArrayList<>();
        for (AQuestionnaire questionnaire : questionnaires) {
            ans.add(AQuestionnaire2Questionnaire(questionnaire));
        }
        return ans;
    }

    public static AQuestionnaire Questionnaire2AQuestionnaire(Questionnaire questionnaire)
    {
        if (questionnaire == null)
            return null;
        return new AQuestionnaire(questionnaire.getId(), User2AUser(questionnaire.getUser()),
                Information2AInformation(questionnaire.getInformation()),
                Information2AInformation(questionnaire.getSearchInformation()),
                Questionnaires2AQuestionnairesShort(questionnaire.getBlackList()),
                Questionnaires2AQuestionnairesShort(questionnaire.getFavList()), questionnaire.isCensored());
    }

    public static List<Questionnaire> AQuestionnaires2QuestionnairesShort(List<AQuestionnaire> questionnaires)
    {
        List<Questionnaire> ans = new ArrayList<>();
        for (AQuestionnaire questionnaire : questionnaires) {
            ans.add(AQuestionnaire2QuestionnaireShort(questionnaire));
        }
        return ans;
    }

    public static Questionnaire AQuestionnaire2Questionnaire(AQuestionnaire questionnaire)
    {
        if (questionnaire == null)
            return null;
        return makeQuestionnaire(questionnaire.getId(), AUser2User(questionnaire.getUser()),
                AInformation2Information(questionnaire.getInformation()),
                AInformation2Information(questionnaire.getSearchInformation()),
                AQuestionnaires2QuestionnairesShort(questionnaire.getBlackList()),
                AQuestionnaires2QuestionnairesShort(questionnaire.getFavList()), questionnaire.isCensored());
    }

    public static Questionnaire AQuestionnaire2QuestionnaireNew(AQuestionnaire questionnaire)
    {
        if (questionnaire == null)
            return null;
        return makeQuestionnaire(null, AUser2User(questionnaire.getUser()),
                AInformation2InformationNew(questionnaire.getInformation()),
                AInformation2InformationNew(questionnaire.getSearchInformation()),
                AQuestionnaires2QuestionnairesShort(questionnaire.getBlackList()),
                AQuestionnaires2QuestionnairesShort(questionnaire.getFavList()), questionnaire.isCensored());
    }

    public static Questionnaire AQuestionnaire2QuestionnaireShort(AQuestionnaire questionnaire)
    {
        if (questionnaire == null)
            return null;
        return makeQuestionnaire(questionnaire.getId(), AUser2User(questionnaire.getUser()),
                AInformation2Information(questionnaire.getInformation()),
                AInformation2Information(questionnaire.getSearchInformation()),
                new ArrayList<Questionnaire>(),
                new ArrayList<Questionnaire>(), questionnaire.isCensored());
    }

    public static AUser User2AUser(User user)
    {
        if (user == null)
            return null;
        return new AUser(user.getId(), user.getName(), user.getAge(), user.isGender(), user.getPassword(), user.getRole());
    }

    public static User AUser2User(AUser user)
    {
        if (user == null)
            return null;
        return makeUser(user.getId(),
                user.getName(), user.getPassword(), user.getAge(), user.isGender());
    }

}
