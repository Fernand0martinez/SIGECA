package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.TeamMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {

    List<TeamMember> findByTeamId(Integer teamId);

    Optional<TeamMember> findByPlayerId(Integer playerId);
}
