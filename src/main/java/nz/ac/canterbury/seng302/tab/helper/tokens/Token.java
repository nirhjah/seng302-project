package nz.ac.canterbury.seng302.tab.helper.tokens;

import java.util.UUID;

/**
 * An abstaract class that Represents a token with a unique ID that can be set
 * and retrieved
 * generating unique tokens is implemented in the subclasses as
 */
public abstract class Token {

    protected String id;

    protected String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * join Team tokens need to be made of only letters and numbers so it is easier
     * to do the same for all tokens
     */
    public void generateToken() {
        this.setId(UUID.randomUUID().toString().replaceAll("\\-*", "").substring(0, 12));
    }

}
