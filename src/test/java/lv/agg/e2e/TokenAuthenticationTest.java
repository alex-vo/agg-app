package lv.agg.e2e;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import lv.agg.dto.JwtAuthenticationResponse;
import lv.agg.dto.ServiceDTO;
import lv.agg.dto.UserProfileDTO;
import lv.agg.entity.UserEntity;
import lv.agg.repository.UserRepository;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
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

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegisterAsIndividualMerchant() {
        given().when()
                .contentType(ContentType.JSON)
                .body(prepareUserProfileDTO("a1@aa.com", "12345", "12345", UserEntity.UserRole.ROLE_MERCHANT, false, "John", "Doe", "blabla", "Wall St. 1"))
                .post("api/v1/user")
                .then().statusCode(201);
        testMerchantLogin("a1@aa.com", "12345");
        UserEntity userEntity = userRepository.findByEmail("a1@aa.com").orElseThrow(RuntimeException::new);
        assertThat(userEntity.getUserRole(), is(UserEntity.UserRole.ROLE_MERCHANT));
        assertThat(userEntity.getMerchantType(), is(UserEntity.MerchantType.INDIVIDUAL));
        assertThat(userEntity.getCompanyName(), is(nullValue()));
        assertThat(userEntity.getFirstName(), is("John"));
        assertThat(userEntity.getLastName(), is("Doe"));
        assertThat(userEntity.getAddress(), is("Wall St. 1"));
    }

    @Test
    public void testRegisterAsCompanyMerchant() {
        given().when()
                .contentType(ContentType.JSON)
                .body(prepareUserProfileDTO("a2@aa.com", "12345", "12345", UserEntity.UserRole.ROLE_MERCHANT, true, "John", "Doe", "company ltd", "Wall St. 1"))
                .post("api/v1/user")
                .then().statusCode(201);
        testMerchantLogin("a2@aa.com", "12345");
        UserEntity userEntity = userRepository.findByEmail("a2@aa.com").orElseThrow(RuntimeException::new);
        assertThat(userEntity.getUserRole(), is(UserEntity.UserRole.ROLE_MERCHANT));
        assertThat(userEntity.getMerchantType(), is(UserEntity.MerchantType.COMPANY));
        assertThat(userEntity.getCompanyName(), is("company ltd"));
        assertThat(userEntity.getFirstName(), is("John"));
        assertThat(userEntity.getLastName(), is("Doe"));
        assertThat(userEntity.getAddress(), is("Wall St. 1"));
    }

    @Test
    public void testRegisterAsCustomer() {
        given().when()
                .contentType(ContentType.JSON)
                .body(prepareUserProfileDTO("a3@aa.com", "12345", "12345", UserEntity.UserRole.ROLE_CUSTOMER, true, "John", "Doe", "company ltd", "Wall St. 1"))
                .post("api/v1/user")
                .then().statusCode(201);
        testMerchantLogin("a3@aa.com", "12345");
        UserEntity userEntity = userRepository.findByEmail("a3@aa.com").orElseThrow(RuntimeException::new);
        assertThat(userEntity.getUserRole(), is(UserEntity.UserRole.ROLE_CUSTOMER));
        assertThat(userEntity.getMerchantType(), is(nullValue()));
        assertThat(userEntity.getCompanyName(), is(nullValue()));
        assertThat(userEntity.getFirstName(), is("John"));
        assertThat(userEntity.getLastName(), is("Doe"));
        assertThat(userEntity.getAddress(), is("Wall St. 1"));
    }

    private void testMerchantLogin(String username, String password) {
        given().when()
                .param("username", username)
                .param("password", "wrong_password")
                .post("api/v1/user/login")
                .then().statusCode(401);

        JwtAuthenticationResponse jwtAuthenticationResponse = given().when()
                .param("username", username)
                .param("password", password)
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
    }

    private UserProfileDTO prepareUserProfileDTO(String email, String password, String passwordRepeat,
                                                 UserEntity.UserRole userRole, boolean isCompany, String firstName,
                                                 String lastName, String companyName, String address) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setPasswordRepeat(passwordRepeat);
        dto.setUserRole(userRole);
        if (userRole == UserEntity.UserRole.ROLE_MERCHANT) {
            dto.setIsCompany(isCompany);
            if (isCompany) {
                dto.setCompanyName(companyName);
            }
        }
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setAddress(address);
        return dto;
    }

    @Test
    public void testFailToGetLoginThenGetThenWaitThenFailToGetThenRefreshTokenThenGet() {
        given().when().get("api/v1/service").then().statusCode(401);
        JwtAuthenticationResponse jwtAuthenticationResponse = given().when()
                .param("username", "merchant@mail.com")
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
