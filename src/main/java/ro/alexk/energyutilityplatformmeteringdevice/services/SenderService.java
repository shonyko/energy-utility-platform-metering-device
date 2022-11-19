package ro.alexk.energyutilityplatformmeteringdevice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.alexk.energyutilityplatformmeteringdevice.config.DeviceConfig;
import ro.alexk.energyutilityplatformmeteringdevice.config.MQConfig;
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
    private final MQConfig mqConfig;

    private final RabbitTemplate rabbitTemplate;

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
                send(measurement);

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

    private void send(Measurement measurement) {
        String data;
        try {
            data = new ObjectMapper().writeValueAsString(measurement);
        } catch (JsonProcessingException e) {
            log.warn("Could not convert measurement to JSON.");
            return;
        }

        try {
            rabbitTemplate.convertAndSend(mqConfig.exchange(), mqConfig.routingKey(), data);
            log.info("Sent: " + measurement);
        } catch (AmqpException e) {
            log.error("Could not send message to the queue: " + e.getMessage());
        }
    }
}
