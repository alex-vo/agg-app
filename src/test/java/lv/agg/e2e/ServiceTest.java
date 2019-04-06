package lv.agg.e2e;

import com.jayway.restassured.RestAssured;
import lv.agg.dto.JwtAuthenticationResponse;
import lv.agg.dto.ServiceDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Stream;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.jwtSecret=trolololololololololololo",
                "app.jwtExpirationInMs=20000"
        })
@RunWith(SpringRunner.class)
public class ServiceTest {

    @LocalServerPort
    public void setPort(int port) {
        RestAssured.port = port;
    }

    @Test
    public void testCreateService() {
        String accessToken = authenticate("admin@mail.com", "123");

        ServiceDTO colorServiceDTO = new ServiceDTO();
        colorServiceDTO.setName("color");

        given().header("Authorization", "Bearer " + accessToken).when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(colorServiceDTO)
                .post("/api/v1/service")
                .then()
                .statusCode(201);

        accessToken = authenticate("merchant@mail.com", "123");
        ServiceDTO[] services = given().header("Authorization", "Bearer " + accessToken).when()
                .get("/api/v1/service")
                .then()
                .statusCode(200)
                .extract().as(ServiceDTO[].class);
        colorServiceDTO = Stream.of(services)
                .filter(s -> "color".equals(s.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        given().header("Authorization", "Bearer " + accessToken).when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("[" + colorServiceDTO.getServiceId() + "]")
                .post("/api/v1/service/set")
                .then()
                .statusCode(200);
        services = given().header("Authorization", "Bearer " + accessToken).when()
                .get("/api/v1/service/get")
                .then()
                .statusCode(200)
                .extract().as(ServiceDTO[].class);
        assertThat(services.length, is(1));
        assertThat(services[0].getName(), is("color"));
        assertThat(services[0].getServiceId(), is(colorServiceDTO.getServiceId()));
    }

    private String authenticate(String email, String password) {
        JwtAuthenticationResponse jwtAuthenticationResponse = given().when()
                .param("username", email)
                .param("password", password)
                .post("api/v1/user/login")
                .then().statusCode(200)
                .extract().as(JwtAuthenticationResponse.class);

        return jwtAuthenticationResponse.getAccessToken();
    }

}


