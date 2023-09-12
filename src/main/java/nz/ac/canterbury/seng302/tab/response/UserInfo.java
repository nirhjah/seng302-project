package nz.ac.canterbury.seng302.tab.response;

import nz.ac.canterbury.seng302.tab.entity.User;

public class UserInfo {

    public String firstName;
    public String lastName;
    public Long userId;

    public UserInfo(User user) {
        firstName = user.getFirstName();
        lastName = user.getLastName();
        userId = user.getId();
    }
}
