package nz.ac.canterbury.seng302.tab.enums;

public enum AuthorityType {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    FEDERATION_MANAGER("ROLE_FEDMAN");

    private String role;

    private AuthorityType(String role) {
        this.role = role;
    }

    public String role() {
        return this.role;
    }
}
