package com.jinha.springboot.surveyapi.survey;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers=SurveyResource.class)
@AutoConfigureMockMvc(addFilters = false)
public class SurveyResourceTest {

    @MockBean
    private SurveyService surveyService;

    @Autowired
    private MockMvc mockMvc;

    private static String SPECIFIC_QUESTION_URL = "http://localhost:8080/surveys/Survey1/questions/Question1";
    private static final String GENERIC_QUESTIONS_URL = "/surveys/Survey1/questions";

    private static final String SPECIFIC_SURVEY_URL = "/surveys/Survey1";

    private static final String GENERIC_SURVEYS_URL = "/surveys";

    @Test
    void retrieveSpecificSurveyQuestion_404Scenario() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());


    }

    @Test
    void retrieveSpecificSurveyQuestion_basicScenario() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON);

        Question question1 = new Question("Question1",
                "Most Popular Cloud Platform Today", Arrays.asList(
                "AWS", "Azure", "Google Cloud", "Oracle Cloud"), "AWS");

        when(surveyService.findQuestionById("Survey1", "Question1")).thenReturn(question1);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String expectedResponse = """
                {
                "id": "Question1",
                "description": "Most Popular Cloud Platform Today",
                "options": [
                "AWS",
                "Azure",
                "Google Cloud",
                "Oracle Cloud"
                ],
                "correctAnswer": "AWS"
                }
                                
                """;

        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(), false);

    }

    @Test
    void retrieveAllSurveyQuestions_basicScenario() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(GENERIC_QUESTIONS_URL).accept(MediaType.APPLICATION_JSON);

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

        when(surveyService.findQuestionsFromSurvey("Survey1")).thenReturn(questions);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String expectedResponse = """
                [
                    {
                        "id": "Question1"
                    },
                    {
                        "id": "Question2"
                    },
                    {
                        "id": "Question3"
                    }
                ]
                                
                """;

        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(), false);
    }

    @Test
    void retrieveAllSurveys_basicScenario() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(GENERIC_SURVEYS_URL).accept(MediaType.APPLICATION_JSON);

        List<Survey> surveys = getSurveyList();

        when(surveyService.getAllSurveys()).thenReturn(surveys);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String expectedResponse = """
                [
                    {
                    "id": "Survey1",
                    "title": "My Favorite Survey",
                    "description": "Description of the Survey"
                    }
                ]    
                """;

        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(), false);
    }

    @Test
    void retrieveSpecificSurvey_basicScenario() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_SURVEY_URL).accept(MediaType.APPLICATION_JSON);

        Survey survey = getSurveyList().get(0);

        when(surveyService.findSurveyById("Survey1")).thenReturn(survey);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String expectedResponse = """
                {
                "id": "Survey1",
                "title": "My Favorite Survey",
                "description": "Description of the Survey"
                }
                """;

        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(), false);
    }

    @Test
    void addNewSurveyQuestion_basicScenario() throws Exception {
        String requestBody = """
                {
                    "description": "Your Favourite Language",
                    "options": [
                        "Java",
                        "Python",
                        "C",
                        "JavaScript"
                    ],
                    "correctAnswer": "JavaScript"
                }
                """;

        when(surveyService.addNewSurveyQuestion(anyString(), any())).thenReturn("SOME_ID");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(GENERIC_QUESTIONS_URL)
                .accept(MediaType.APPLICATION_JSON).content(requestBody).contentType(MediaType.APPLICATION_JSON);


        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String locationHeader = mvcResult.getResponse().getHeader("Location");

        assertEquals(201, mvcResult.getResponse().getStatus());
        assertTrue(locationHeader.contains("/surveys/Survey1/questions/SOME_ID"));
    }

    private static List<Survey> getSurveyList() {
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

        return new ArrayList<>(List.of(survey));
    }


}
