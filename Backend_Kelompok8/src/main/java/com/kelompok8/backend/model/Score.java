package com.kelompok8.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "score_id")
    private UUID scoreId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "completion_time", nullable = false)
    private Double completionTime;

    @Column(name = "remaining_hp", nullable = false)
    private Double remainingHp;

    @CreationTimestamp
    @Column(name = "date_achieved", updatable = false)
    private LocalDateTime createdAt;

    public Score() {}

    public Score(String playerName, Double completionTime, Double remainingHp) {
        this.playerName = playerName;
        this.completionTime = completionTime;
        this.remainingHp = remainingHp;
    }

    public UUID getScoreId() {
        return scoreId;
    }

    public void setScoreId(UUID scoreId) {
        this.scoreId = scoreId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Double getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Double completionTime) {
        this.completionTime = completionTime;
    }

    public Double getRemainingHp() {
        return remainingHp;
    }

    public void setRemainingHp(Double remainingHp) {
        this.remainingHp = remainingHp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
