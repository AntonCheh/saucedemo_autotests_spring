package selenium.sources.loginTestEffectiveMobile;

public record LoginTestsSource(String login,
                               String password) {

    public static Builder builder () {
        return new Builder();
    }

    public static class Builder {
        private String login;
        private String password;


    public Builder login (String login) {
        this.login = login;
        return this;
    }

    public Builder password (String password) {
        this.password = password;
        return this;
    }

    public LoginTestsSource build () {
        return new LoginTestsSource(login, password);
    }

}
}
