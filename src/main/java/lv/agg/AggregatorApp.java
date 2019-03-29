package lv.agg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:local.properties")
public class AggregatorApp {

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApp.class);
    }

}
