package com.spring.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScoreController {

    private final ScoreProcessor scoreProcessor;

    @Autowired
    public ScoreController(ScoreProcessor scoreProcessor) {
        this.scoreProcessor = scoreProcessor;
    }

//    // ********* DEPRECATED ********* //
//    @GetMapping("/scores/high-scores")
//    public String[] getScoresLowToHigh() {
//        return scoreProcessor.getOverallScoresHighToLow();
//    }

    @GetMapping("/scores/high-scores/{category}")
    public String[][] getCategoryScoresLowToHigh(@PathVariable String category) {
        return scoreProcessor.getCategoryScoresHighToLow(category);
    }

    @PostMapping("/scores/submit-score/{category}")
    public void updateScores(@PathVariable String category, @RequestParam String initials, @RequestParam int score) {
        scoreProcessor.updateUserScore(category, initials, score);
    }
}
