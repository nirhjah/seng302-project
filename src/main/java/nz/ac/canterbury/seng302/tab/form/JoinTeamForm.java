package nz.ac.canterbury.seng302.tab.form;


import jakarta.validation.constraints.NotNull;

public class JoinTeamForm {

    @NotNull
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
