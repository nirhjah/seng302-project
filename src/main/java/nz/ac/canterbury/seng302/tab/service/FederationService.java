package nz.ac.canterbury.seng302.tab.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.FederationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FederationService {

    @Autowired
    FederationRepository federationRepository;

    @Autowired
    EmailService emailService;

    public User getUserByFedInviteToken(String token) {
        return federationRepository.getFederationManagerInviteByToken(token).getUser();
    }

    public FederationManagerInvite getByToken(String token) {
        return federationRepository.getFederationManagerInviteByToken(token);
    }

    public void updateOrSave(FederationManagerInvite invite) {
        federationRepository.save(invite);
    }

    public void delete(FederationManagerInvite invite) {
        federationRepository.delete(invite);
    }
}
