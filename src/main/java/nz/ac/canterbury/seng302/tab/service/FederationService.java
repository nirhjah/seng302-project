package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.repository.FederationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FederationService {

    @Autowired
    FederationRepository federationRepository;

    public FederationManagerInvite getByToken(String token) {
        return federationRepository.getFederationManagerInviteByToken(token);
    }

    public void updateOrSave(FederationManagerInvite invite) {
        federationRepository.save(invite);
    }

    public void delete(FederationManagerInvite invite) {
        federationRepository.delete(invite);
    }

    /**
     * Finds the user in the FederationManagerInvite entity
     * @param user user that is searched in the federation manager invite entity
     * @return optional FederationManagerinvite variable containing the user
     */
    public Optional<FederationManagerInvite> findFederationManagerByUser(User user) {
        return federationRepository.findByUser(user);
    }
}
