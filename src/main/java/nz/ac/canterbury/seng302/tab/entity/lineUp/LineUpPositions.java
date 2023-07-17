package nz.ac.canterbury.seng302.tab.entity.lineUp;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;

import java.util.Set;

@Entity(name="LineUpPositions")

public class LineUpPositions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineUpPositionsId;

    @OneToOne
    @JoinColumn(name = "fk_lineUpId", referencedColumnName = "lineUpId")
    private LineUp lineUp;

    @OneToOne
    @JoinColumn(name = "fk_userId", referencedColumnName = "userId")
    private User player;

    @Column(nullable = false)
    private int position;

}
