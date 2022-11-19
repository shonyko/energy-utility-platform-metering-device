package ro.alexk.energyutilityplatformmeteringdevice.models;

public record Measurement(
        Long timestamp,
        String device_id,
        Double measurement_value
) {
}
