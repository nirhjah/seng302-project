package nz.ac.canterbury.seng302.tab.form;


import jakarta.validation.constraints.NotBlank;

public class JoinTeamForm {

    @NotBlank(message = "Field cannot be blank")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
