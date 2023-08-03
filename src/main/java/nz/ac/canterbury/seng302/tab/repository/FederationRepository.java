package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import org.springframework.data.repository.CrudRepository;

public interface FederationRepository extends CrudRepository<FederationManagerInvite, Long> {

    FederationManagerInvite getFederationManagerInviteByToken(String token);
}
