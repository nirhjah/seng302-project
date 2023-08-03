package nz.ac.canterbury.seng302.tab.enums;

/**
 * The types of authority (Global roles) used in this system.
 */
public enum AuthorityType {
    /** The default authority for every signed-in user */
    USER(Constants.USER),
    /** The highest level of permission */
    ADMIN(Constants.ADMIN),
    /** Users who can access federation manager level endpoints (All related to competitions) */
    FEDERATION_MANAGER(Constants.FEDERATION_MANAGER);

    private String role;

    private AuthorityType(String role) {
        this.role = role;
    }

    /**
     * <p>Gets the Spring Security compatible string of these roles.</p>
     * e.g. <code>"ROLE_" + roleName</code>
     */
    public String role() {
        return this.role;
    }

    /**
     * The underlying role string constants, acceptable by Spring Security.
     */
    public class Constants {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String FEDERATION_MANAGER = "ROLE_FEDERATION_MANAGER";
        
        private Constants() {/* Hiding the constructor */}
    }
}
