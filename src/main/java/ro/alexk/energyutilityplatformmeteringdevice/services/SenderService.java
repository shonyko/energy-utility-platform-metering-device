package ro.alexk.energyutilityplatformmeteringdevice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.alexk.energyutilityplatformmeteringdevice.config.DeviceConfig;
import ro.alexk.energyutilityplatformmeteringdevice.models.Measurement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SenderService implements CommandLineRunner {

    private final DeviceConfig deviceConfig;

    @Override
    public void run(String... args) {
        log.info("Starting producer...");

        try (var reader = new BufferedReader(new InputStreamReader(deviceConfig.data().getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                double measurementValue;

                try {
                    measurementValue = Double.parseDouble(line);
                } catch (NumberFormatException e) {
                    log.warn("Invalid data: " + line);
                    continue;
                }

                var timestamp = Instant.now().toEpochMilli();

                var measurement = new Measurement(timestamp, deviceConfig.id(), measurementValue);
                System.out.println(measurement);
                // TODO: send to rabbitMQ

                TimeUnit.MILLISECONDS.sleep(deviceConfig.delay());
            }
        } catch (IOException e) {
            log.error("Data file could not be found.");
            log.error(e.getMessage());
            return;
        } catch (InterruptedException e) {
            log.warn("Thread was interrupted.");
            Thread.currentThread().interrupt();
            return;
        }

        log.info("Sent data successfully. Shutting down...");
    }
}
