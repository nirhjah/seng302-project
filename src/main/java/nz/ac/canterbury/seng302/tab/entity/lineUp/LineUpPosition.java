package nz.ac.canterbury.seng302.tab.entity.lineUp;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * Line-up position entity for storing information about the position of a player in a line-up
 */
@Entity(name="LineUpPosition")
public class LineUpPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineUpPositionId;

    @OneToOne
    @JoinColumn(name = "lineUpId")
    private LineUp lineUp;

    @OneToOne
    @JoinColumn(name = "userId")
    private User player;

    @Column(nullable = false)
    private int position;

    /**
     * Default constructor for Line-up positions.
     * Required by JPA.
     */
    protected LineUpPosition() {}

    /**
     * Constructs a position in a line-up for a player to occupy
     *
     * @param lineUp    Line-up for the player to exist in
     * @param player    The player to whom the position describes
     * @param position  The position in the formation as it relates to the line-up
     */
    public LineUpPosition(LineUp lineUp, User player, int position) {
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

    public Long getLineUpPositionId() {
        return lineUpPositionId;
    }

    public void setLineUpPositionId(Long lineUpPositionId) {
        this.lineUpPositionId = lineUpPositionId;
    }
}
