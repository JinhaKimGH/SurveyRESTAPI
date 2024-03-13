package com.jinha.springboot.surveyapi.survey;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
public class SurveyResource {

    private SurveyService surveyService;

    public SurveyResource(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /*
     * Retrieves all surveys
     */
    @RequestMapping("/surveys")
    public List<Survey> getAllSurveys(){
        return surveyService.getAllSurveys();
    }

    /*
     * Retrieves survey from the surveyId
     */
    @RequestMapping("/surveys/{surveyId}")
    public Survey getSurveyById(@PathVariable String surveyId){

        Survey survey = surveyService.findSurveyById(surveyId);

        if(survey == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return survey;
    }

    /*
     * Retrieves survey's questions from the surveyId
     */
    @RequestMapping("/surveys/{surveyId}/questions")
    public List<Question> getQuestionsFromSurvey(@PathVariable String surveyId){

        List<Question> questions = surveyService.findQuestionsFromSurvey(surveyId);

        if(questions == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return questions;
    }

    /*
     * Retrieves a survey's specific question
     */
    @RequestMapping("/surveys/{surveyId}/questions/{questionId}")
    public Question getQuestionsByIdFromSurvey(@PathVariable String surveyId, @PathVariable String questionId){

        Question question = surveyService.findQuestionById(surveyId, questionId);

        if(question == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return question;
    }

    /*
     * Adds a question to the survey
     */
    @RequestMapping(value="/surveys/{surveyId}/questions", method= RequestMethod.POST)
    public ResponseEntity<Object> addNewSurveyQuestion(@PathVariable String surveyId,
                                                       @RequestBody Question question){

        String questionId = surveyService.addNewSurveyQuestion(surveyId, question);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{questionId}").buildAndExpand(questionId).toUri()).build();
    }

    @RequestMapping(value="/surveys/{surveyId}/questions/{questionId}", method=RequestMethod.DELETE)
    public ResponseEntity<Object> deleteSurveyQuestion(@PathVariable String surveyId,
                                         @PathVariable String questionId) {
        String id = surveyService.deleteSurveyQuestion(surveyId, questionId);

        if( id == null )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);


        return ResponseEntity.created( ServletUriComponentsBuilder.fromCurrentRequest().path("/{questionId}")
                .buildAndExpand(questionId).toUri()).build();
    }

    @RequestMapping(value="/surveys/{surveyId}/questions/{questionId}", method=RequestMethod.PUT)
    public ResponseEntity<Object> updateSurveyQuestion(@PathVariable String surveyId,
                                                       @PathVariable String questionId,
                                                       @RequestBody Question question) {
        String id = surveyService.updateSurveyQuestion(surveyId, questionId, question);

        if( id == null )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);


        return ResponseEntity.noContent().build();
    }

}
