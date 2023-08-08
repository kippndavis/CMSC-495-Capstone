package com.spring.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {

    private final QuestionProcessor questionProcessor;

    @Autowired
    public QuestionController(QuestionProcessor questionProcessor) {
        this.questionProcessor = questionProcessor;
    }

    @GetMapping("/questions/random/{category}")
    public String[][] getRandomQuestionsByCategory(@PathVariable String category) {
        return questionProcessor.getRandomQuestionsByCategory(category);
    }

    @GetMapping("/questions/random/{category}/{numberOfQuestions}")
    public String[][] getRandomQuestionsByCategoryAndNumber(@PathVariable String category, @PathVariable int numberOfQuestions) {
        return questionProcessor.getRandomQuestionsByCategory(category, numberOfQuestions);
    }

    @PostMapping("/questions/gpt")
    public String getOpenAIGeneratedQuestions(@RequestParam String category) {
        return questionProcessor.generateMultipleChoiceQuestions(category);
    }
}
