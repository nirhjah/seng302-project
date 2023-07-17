package nz.ac.canterbury.seng302.tab.entity.lineUp;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.User;

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

    /**
     * Default constructor for Line-up positions.
     * Required by JPA.
     */
    protected LineUpPositions() {}

    /**
     * Constructs a position in a line-up for a player to occupy
     *
     * @param lineUp    Line-up for the player to exist in
     * @param player    The player to whom the position describes
     * @param position  The position in the formation as it relates to the line-up
     */
    public LineUpPositions(LineUp lineUp, User player, int position) {
        this.lineUp = lineUp;
        this.player = player;
        this.position = position;
    }


    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LineUp getLineUp() {
        return lineUp;
    }

    public void setLineUp(LineUp lineUp) {
        this.lineUp = lineUp;
    }
}
