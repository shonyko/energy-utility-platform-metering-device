package ro.alexk.energyutilityplatformmeteringdevice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.core.io.Resource;

@ConstructorBinding
@ConfigurationProperties(prefix = "device")
public record DeviceConfig(
        String id,
        Long delay,
        Resource data
) {
}
