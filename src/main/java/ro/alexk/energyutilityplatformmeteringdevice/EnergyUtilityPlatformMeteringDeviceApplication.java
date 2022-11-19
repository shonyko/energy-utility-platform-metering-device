package ro.alexk.energyutilityplatformmeteringdevice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EnergyUtilityPlatformMeteringDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyUtilityPlatformMeteringDeviceApplication.class, args);
    }

}
