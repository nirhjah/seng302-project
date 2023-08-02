package nz.ac.canterbury.seng302.tab.enums;

public enum AuthorityType {
    USER(Constants.USER),
    ADMIN(Constants.ADMIN),
    FEDERATION_MANAGER(Constants.FEDERATION_MANAGER);

    private String role;

    private AuthorityType(String role) {
        this.role = role;
    }

    public String role() {
        return this.role;
    }

    public class Constants {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String FEDERATION_MANAGER = "ROLE_FEDERATION_MANAGER";
        
        private Constants() {/* Hiding the constructor */}
    }
}
