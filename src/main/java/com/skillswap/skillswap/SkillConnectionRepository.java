package com.skillswap.skillswap;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillConnectionRepository
        extends JpaRepository<SkillConnection, Long> {

    List<SkillConnection> findByRequester_IdOrRecipient_Id(
            Long requesterId,
            Long recipientId
    );
}