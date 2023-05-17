package nz.ac.canterbury.seng302.tab.enums;

import java.util.HashMap;
import java.util.Map;

public enum Role {

    MANAGER,
    COACH,
    MEMBER;


    private static final Map<String, Role> ROLE_MAPPING = new HashMap<>();
    static {
        for (var role: Role.values()) {
            ROLE_MAPPING.put(role.toString(), role);
        }
    }

    public static Role stringToRole(String role) {
        return ROLE_MAPPING.get(role);
    }

    public static boolean isValidRole(String role) {
        return ROLE_MAPPING.containsKey(role);
    }
}
