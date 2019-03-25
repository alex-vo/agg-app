package lv.agg.e2e;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import lv.agg.dto.AppointmentDTO;
import lv.agg.dto.AvailabilitySlotDTO;
import lv.agg.dto.TimeSlotDTO;
import lv.agg.entity.AppointmentEntity;
import lv.agg.repository.ServiceRepository;
import lv.agg.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.jayway.restassured.RestAssured.given;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class AppointmentTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    @LocalServerPort
    public void setPort(int port) {
        RestAssured.port = port;
    }

    @Test
    public void testCreateAppointment() {
        Long serviceId = serviceRepository.findASDdasdasdsad("haircut").get().getId();
        Long serviceProviderId = userRepository.findByEmail("service_provider@mail.com").get().getId();

        AvailabilitySlotDTO availabilitySlotDTO = new AvailabilitySlotDTO();
        TimeSlotDTO timeSlotDTO = new TimeSlotDTO();
        timeSlotDTO.setDateFrom(ZonedDateTime.now().minusHours(5));
        timeSlotDTO.setDateTo(ZonedDateTime.now().plusHours(5));
        availabilitySlotDTO.setTimeSlotDTO(timeSlotDTO);
        availabilitySlotDTO.setServiceId(serviceId);
        availabilitySlotDTO.setUserId(serviceProviderId);
        given().auth().preemptive().basic("service_provider", "123").when()
                .contentType(ContentType.JSON)
                .body(availabilitySlotDTO)
                .post("/api/v1/availability")
                .then()
                .statusCode(201);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setServiceProviderId(serviceProviderId);
        appointmentDTO.setServiceId(serviceId);
        appointmentDTO.setFrom(ZonedDateTime.now().minusHours(1));
        appointmentDTO.setTo(ZonedDateTime.now().plusHours(1));
        given().auth().preemptive().basic("service_provider", "123").when()
                .contentType(ContentType.JSON)
                .body(appointmentDTO)
                .post("/api/v1/customer/appointment")
                .then()
                .statusCode(403);
        Response createdAppointmentResponse = given().auth().preemptive().basic("customer", "123").when()
                .contentType(ContentType.JSON)
                .body(appointmentDTO)
                .post("/api/v1/customer/appointment");
        createdAppointmentResponse.then().statusCode(201);
        String location = createdAppointmentResponse.getHeader(HttpHeaders.LOCATION);
        Long createdAppointmentId = Long.valueOf(location.substring(StringUtils.lastIndexOf(location, '/') + 1));
        appointmentDTO.setFrom(appointmentDTO.getFrom().plusHours(1));
        appointmentDTO.setTo(appointmentDTO.getTo().plusHours(1));
        given().auth().preemptive().basic("customer", "123").when()
                .contentType(ContentType.JSON)
                .body(appointmentDTO)
                .post("/api/v1/customer/appointment")
                .then()
                .statusCode(201);
        AppointmentDTO[] foundAppointments = given().auth().preemptive().basic("customer", "123").when()
                .get("/api/v1/customer/appointment?serviceProviderId=" + serviceProviderId
                        + "&dateFrom=" + ISO_DATE_TIME.format(ZonedDateTime.now().minusHours(2))
                        + "&dateTo=" + ISO_DATE_TIME.format(ZonedDateTime.now().plusHours(2))
                )
                .then()
                .statusCode(200).extract().as(AppointmentDTO[].class);
        assertThat(foundAppointments.length, is(2));
        assertThat(Stream.of(foundAppointments).allMatch(o -> AppointmentEntity.Status.NEW.name().equals(o.getStatus())), is(true));
        List timeSlots = given().auth().preemptive().basic("customer", "123").when()
                .get("/api/v1/availability?serviceId=" + serviceId
                        + "&dateFrom=" + ISO_DATE_TIME.format(ZonedDateTime.now().minusDays(1))
                        + "&dateTo=" + ISO_DATE_TIME.format(ZonedDateTime.now().plusDays(1)))
                .then()
                .statusCode(200).extract().as(List.class);
        assertThat((List<TimeSlotDTO>) timeSlots, Matchers.hasSize(1));

        //Appointment get confirmed by service provider
        given().auth().preemptive().basic("service_provider", "123").when()
                .contentType(ContentType.JSON)
                .body(appointmentDTO)
                .post("/api/v1/serviceprovider/appointment/" + createdAppointmentId + "/confirm")
                .then()
                .statusCode(201);
        given().auth().preemptive().basic("customer", "123").when()
                .contentType(ContentType.JSON)
                .body(appointmentDTO)
                .post("/api/v1/customer/appointment")
                .then()
                .statusCode(500);
        AppointmentDTO confirmedAppointment = given().auth().preemptive().basic("customer", "123").when()
                .get("/api/v1/customer/appointment/" + createdAppointmentId)
                .then()
                .statusCode(200).extract().as(AppointmentDTO.class);
        assertThat(confirmedAppointment.getStatus(), is(AppointmentEntity.Status.CONFIRMED.name()));
        timeSlots = given().auth().preemptive().basic("customer", "123").when()
                .get("/api/v1/availability?serviceId=" + serviceId
                        + "&dateFrom=" + ISO_DATE_TIME.format(ZonedDateTime.now().minusDays(1))
                        + "&dateTo=" + ISO_DATE_TIME.format(ZonedDateTime.now().plusDays(1)))
                .then()
                .statusCode(200).extract().as(List.class);
        assertThat((List<TimeSlotDTO>) timeSlots, Matchers.hasSize(2));
    }

}
