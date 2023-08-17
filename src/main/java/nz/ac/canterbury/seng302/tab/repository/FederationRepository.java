package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FederationRepository extends CrudRepository<FederationManagerInvite, Long> {
    Optional<FederationManagerInvite> findByUser(User user);

    FederationManagerInvite getFederationManagerInviteByToken(String token);
}
