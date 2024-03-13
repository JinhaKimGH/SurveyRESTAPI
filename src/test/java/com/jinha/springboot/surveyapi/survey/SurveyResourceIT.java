package com.jinha.springboot.surveyapi.survey;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SurveyResourceIT {

    private static final String SPECIFIC_QUESTION_URL = "/surveys/Survey1/questions/Question1";

    private static final String GENERIC_QUESTIONS_URL = "/surveys/Survey1/questions";

    private static final String SPECIFIC_SURVEY_URL = "/surveys/Survey1";

    private static final String GENERIC_SURVEYS_URL = "/surveys";

    @Autowired
    private TestRestTemplate template;
    String str = """
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

    @Test
    void retrieveSpecificSurveyQuestion_basicScenario() throws JSONException {
        HttpHeaders headers = createHttpContentTypeAndAuthorizationHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> responseEntity = template.exchange(SPECIFIC_QUESTION_URL, HttpMethod.GET, httpEntity, String.class);

        String expectedResponse = """
                {"id":"Question1","description":"Most Popular Cloud Platform Today","options":["AWS","Azure","Google Cloud","Oracle Cloud"],"correctAnswer":"AWS"}
                """;

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), false);
    }

    @Test
    void retrieveAllSurveyQuestions_basicScenario() throws JSONException {
        HttpHeaders headers = createHttpContentTypeAndAuthorizationHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> responseEntity = template.exchange(GENERIC_QUESTIONS_URL, HttpMethod.GET, httpEntity, String.class);

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

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), false);
    }

    @Test
    void retrieveSpecificSurvey_basicScenario() throws JSONException {
        HttpHeaders headers = createHttpContentTypeAndAuthorizationHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> responseEntity = template.exchange(SPECIFIC_SURVEY_URL, HttpMethod.GET, httpEntity, String.class);

        String expectedResponse = """
                {
                    "id": "Survey1",
                    "title": "My Favorite Survey",
                    "description": "Description of the Survey",
                    "questions": [
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
                }
                                
                """;
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), false);
    }

    @Test
    void retrieveAllSurveyQuestions() throws JSONException {
        HttpHeaders headers = createHttpContentTypeAndAuthorizationHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> responseEntity = template.exchange(GENERIC_SURVEYS_URL, HttpMethod.GET, httpEntity, String.class);

        String expectedResponse = """
                [
                    {
                        "id": "Survey1"
                    }
                ]
                                
                """;

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("application/json", responseEntity.getHeaders().get("Content-Type").get(0));
        JSONAssert.assertEquals(expectedResponse, responseEntity.getBody(), false);
    }

    @Test
    void createNewSurveyQuestion() throws JSONException {
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
        HttpHeaders headers = createHttpContentTypeAndAuthorizationHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody, headers);

        ResponseEntity<String> responseEntity = template.exchange(GENERIC_QUESTIONS_URL, HttpMethod.POST, httpEntity, String.class);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        String locationHeader = responseEntity.getHeaders().get("Location").get(0);
        assertTrue(locationHeader.contains("/surveys/Survey1/questions"));

        // DELETE
        ResponseEntity<String> responseEntityDelete = template.exchange(locationHeader, HttpMethod.DELETE, httpEntity, String.class);
        assertTrue(responseEntityDelete.getStatusCode().is2xxSuccessful());

    }

    private HttpHeaders createHttpContentTypeAndAuthorizationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Basic " + performBasicAuthEncoding("admin","password"));
        return headers;
    }

    String performBasicAuthEncoding(String user, String password) {
        String combined = user + ":" + password;
        return new String(Base64.getEncoder().encode(combined.getBytes()));
    }
}
