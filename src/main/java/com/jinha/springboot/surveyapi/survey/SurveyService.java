package com.jinha.springboot.surveyapi.survey;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class SurveyService {
    private static List<Survey> surveys = new ArrayList<>();

    static {
        Question question1 = new Question("Question1",
                "Most Popular Cloud Platform Today", Arrays.asList(
                "AWS", "Azure", "Google Cloud", "Oracle Cloud"), "AWS");
        Question question2 = new Question("Question2",
                "Fastest Growing Cloud Platform", Arrays.asList(
                "AWS", "Azure", "Google Cloud", "Oracle Cloud"), "Google Cloud");
        Question question3 = new Question("Question3",
                "Most Popular DevOps Tool", Arrays.asList(
                "Kubernetes", "Docker", "Terraform", "Azure DevOps"), "Kubernetes");

        List<Question> questions = new ArrayList<>(Arrays.asList(question1,
                question2, question3));

        Survey survey = new Survey("Survey1", "My Favorite Survey",
                "Description of the Survey", questions);

        surveys.add(survey);

    }

    public List<Survey> getAllSurveys() {
        return surveys;
    }

    public Survey findSurveyById(String id){
        Predicate<? super Survey> predicate = survey -> survey.getId().equalsIgnoreCase(id);

        Optional<Survey> optionalSurvey = surveys.stream().filter(predicate).findFirst();

        return optionalSurvey.orElse(null);

    }

    public List<Question> findQuestionsFromSurvey(String id){
        Survey survey = findSurveyById(id);

        if(survey == null){
            return null;
        }

        return survey.getQuestions();
    }

    public Question findQuestionById(String surveyId, String questionId){
        List<Question> questions = findQuestionsFromSurvey(surveyId);

        if(questions == null){
            return null;
        }

        Predicate<? super Question> questionPredicate = question -> question.getId().equalsIgnoreCase(questionId);

        Optional<Question> optionalQuestion = questions.stream().filter(questionPredicate).findFirst();

        return optionalQuestion.orElse(null);

    }

    public String addNewSurveyQuestion(String surveyId, Question question) {
        List<Question> questions = findQuestionsFromSurvey(surveyId);
        String id = getRandomId();
        question.setId(id);
        questions.add(question);
        return id;
    }

    private static String getRandomId() {
        SecureRandom secureRandom = new SecureRandom();
        return new BigInteger(32, secureRandom).toString();
    }

    public String deleteSurveyQuestion(String surveyId, String questionId){
        List<Question> questions = findQuestionsFromSurvey(surveyId);

        if(questions == null){
            return null;
        }

        Predicate<? super Question> questionPredicate = question -> question.getId().equalsIgnoreCase(questionId);
        boolean removed = questions.removeIf(questionPredicate);

        if(!removed) {
            return null;
        }

        return questionId;

    }

    public String updateSurveyQuestion(String surveyId, String questionId, Question question) {
        List<Question> questions = findQuestionsFromSurvey(surveyId);

        if(questions == null){
            return null;
        }

        questions.removeIf(q -> q.getId().equalsIgnoreCase(questionId));
        questions.add(question);
        return questionId;
    }
}
