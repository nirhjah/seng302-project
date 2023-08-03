package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.FederationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FederationService {

    @Autowired
    FederationRepository federationRepository;

    public User getUserByFedInviteToken(String token) {
        return federationRepository.getFederationManagerInviteByToken(token).getUser();
    }
}
