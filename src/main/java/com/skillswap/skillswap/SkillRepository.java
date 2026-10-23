
package com.skillswap.skillswap;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUser_Id(Long userId);

    List<Skill> findBySkillTypeIgnoreCase(String skillType);

}
