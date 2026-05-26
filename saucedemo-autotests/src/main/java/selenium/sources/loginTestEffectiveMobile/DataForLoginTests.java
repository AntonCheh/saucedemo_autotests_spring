package selenium.sources.loginTestEffectiveMobile;

import java.util.stream.Stream;

public interface DataForLoginTests {

    static Stream<LoginTestsSource> dataSuccessful(){
        return Stream.of(LoginTestsSource.builder()
                .login("standard_user")
                .password("secret_sauce").build());
    }

    static Stream<LoginTestsSource> dataWrongPassword(){
        return Stream.of(LoginTestsSource.builder()
                .login("standard_user")
                .password("secret").build());
    }

    static Stream<LoginTestsSource> dataEmpty(){
        return Stream.of(LoginTestsSource.builder()
                .login("")
                .password("").build());
    }

    static Stream<LoginTestsSource> dataBlockedUser(){
        return Stream.of(LoginTestsSource.builder()
                .login("locked_out_user")
                .password("secret_sauce").build());
    }

    static Stream<LoginTestsSource> dataWithTimeOut(){
        return Stream.of(LoginTestsSource.builder()
                .login("performance_glitch_user")
                .password("secret_sauce").build());
    }


}
