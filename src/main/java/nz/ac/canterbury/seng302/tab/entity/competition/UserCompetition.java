package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.User;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for user Competition - competition composed of players only
 */
@Entity 
@DiscriminatorValue("USER")
public class UserCompetition extends Competition {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_competition_players",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<>();
}
