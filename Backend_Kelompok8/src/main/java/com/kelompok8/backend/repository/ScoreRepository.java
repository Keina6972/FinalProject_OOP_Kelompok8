package com.kelompok8.backend.repository;

import com.kelompok8.backend.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
    List<Score> findAllByOrderByCompletionTimeAscRemainingHpDesc();
}
