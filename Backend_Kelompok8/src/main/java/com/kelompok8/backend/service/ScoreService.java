package com.kelompok8.backend.service;

import com.kelompok8.backend.model.Score;
import com.kelompok8.backend.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScoreService {
    @Autowired
    private ScoreRepository scoreRepository;

    public Score createScore(Score score) {
        if (score.getCompletionTime() == null || score.getCompletionTime() <= 0) {
            throw new IllegalArgumentException("Completion time must be greater than 0");
        }
        if (score.getRemainingHp() == null || score.getRemainingHp() < 0) {
            throw new IllegalArgumentException("Remaining HP cannot be negative");
        }
        if (score.getPlayerName() == null || score.getPlayerName().trim().isEmpty()) {
            score.setPlayerName("Player");
        }
        return scoreRepository.save(score);
    }

    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    public List<Score> getLeaderboard() {
        return scoreRepository.findAllByOrderByCompletionTimeAscRemainingHpDesc();
    }

    public void deleteScore(UUID id) {
        if (!scoreRepository.existsById(id)) {
            throw new RuntimeException("Score not found with id: " + id);
        }
        scoreRepository.deleteById(id);
    }
}
