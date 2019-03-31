package lv.agg.e2e;

import com.jayway.restassured.RestAssured;
import lv.agg.dto.JwtAuthenticationResponse;
import lv.agg.dto.ServiceDTO;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.jwtSecret=trolololololololololololo",
                "app.jwtExpirationInMs=5000"
        })
@RunWith(SpringRunner.class)
public class TokenAuthenticationTest {
    @LocalServerPort
    public void setPort(int port) {
        RestAssured.port = port;
    }

    @Test
    public void testFailToGetLoginThenGetThenWaitThenFailToGetThenRefreshTokenThenGet() {
        given().when().get("api/v1/service").then().statusCode(401);
        JwtAuthenticationResponse jwtAuthenticationResponse = given().when()
                .param("username", "service_provider@mail.com")
                .param("password", "123")
                .post("api/v1/user/login")
                .then().statusCode(200)
                .extract().as(JwtAuthenticationResponse.class);
        String accessToken = jwtAuthenticationResponse.getAccessToken();
        String refreshToken = jwtAuthenticationResponse.getRefreshToken();

        assertThat(accessToken, is(notNullValue()));
        assertThat(refreshToken, is(notNullValue()));

        ServiceDTO[] services = given().header("Authorization", "Bearer " + accessToken).when().get("api/v1/service")
                .then().statusCode(200)
                .extract().as(ServiceDTO[].class);
        assertThat(services.length, is(1));
        assertThat(services[0].getName(), is("haircut"));

        Awaitility.await().atMost(6, TimeUnit.SECONDS).until(() -> given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("api/v1/service")
                .then().extract().statusCode() == 401);

        jwtAuthenticationResponse = given().when().param("refreshToken", refreshToken).post("api/v1/user/refresh")
                .then().statusCode(200)
                .extract().as(JwtAuthenticationResponse.class);
        String newAccessToken = jwtAuthenticationResponse.getAccessToken();
        String newRefreshToken = jwtAuthenticationResponse.getRefreshToken();

        assertThat(newAccessToken, is(notNullValue()));
        assertThat(newRefreshToken, is(notNullValue()));

        services = given().header("Authorization", "Bearer " + newAccessToken).when().get("api/v1/service")
                .then().statusCode(200)
                .extract().as(ServiceDTO[].class);
        assertThat(services.length, is(1));
        assertThat(services[0].getName(), is("haircut"));

    }
}
