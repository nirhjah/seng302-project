package nz.ac.canterbury.seng302.tab.helper.interfaces;

public interface Identifiable {
    /**
     * Gets the Id of the object.
     * This would usually be the PK of a JPA entity.
     * @return the id.
     */
    long getId();
}
