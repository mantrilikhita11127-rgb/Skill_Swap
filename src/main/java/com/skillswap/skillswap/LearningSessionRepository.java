package com.skillswap.skillswap;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningSessionRepository
        extends JpaRepository<LearningSession, Long> {

    List<LearningSession>
    findByRequester_IdOrRecipient_IdOrderByStartTimeAsc(
            Long requesterId,
            Long recipientId
    );
}