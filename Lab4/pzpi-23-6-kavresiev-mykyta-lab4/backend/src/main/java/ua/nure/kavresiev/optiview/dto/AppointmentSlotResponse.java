package ua.nure.kavresiev.optiview.dto;

public class AppointmentSlotResponse {

    private String time;
    private String label;
    private boolean available;

    public AppointmentSlotResponse(String time, String label, boolean available) {
        this.time = time;
        this.label = label;
        this.available = available;
    }

    public String getTime() {
        return time;
    }

    public String getLabel() {
        return label;
    }

    public boolean isAvailable() {
        return available;
    }
}