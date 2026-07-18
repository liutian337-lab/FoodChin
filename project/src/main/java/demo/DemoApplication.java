package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import demo.config.BlockchainProperties;
import demo.config.AIProperties;

@SpringBootApplication
@EnableConfigurationProperties({BlockchainProperties.class, AIProperties.class})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
