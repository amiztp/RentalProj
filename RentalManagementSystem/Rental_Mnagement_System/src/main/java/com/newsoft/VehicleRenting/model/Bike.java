package main.java.com.newsoft.VehicleRenting.model;
//import java.util.Objects;

public class Bike extends Vehicle {

    private String model;

    /**
     * Full constructor.
     */
    public Bike(String model, String registerNumber, String color) {
        super(requireNonBlank(registerNumber, "registerNumber"),
              requireNonBlank(color, "color"));
        this.model = requireNonBlank(model, "model");
    }

    /**
     * Simple constructor for CSV loading.
     */
    public Bike(String registerNumber, String color) {
        super(requireNonBlank(registerNumber, "registerNumber"),
              requireNonBlank(color, "color"));
        this.model = "Unknown";
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    public String getModel() { return model; }

    public void displayInfo() {
        System.out.println(this.toString());
        System.out.println("Model: " + model);
        System.out.println("Register: " + getRegisterNumber() + ", Color: " + getColor());

        if (!getPhotoPaths().isEmpty()) {
            System.out.println("Photos:");
            getPhotoPaths().forEach(p -> System.out.println("  - " + p));
        } else {
            System.out.println("No photos available.");
        }
    }

    @Override
    public String toString() {
        return String.format("Bike[reg=%s, model=%s, color=%s, photos=%d]",
                getRegisterNumber(), model, getColor(), getPhotoPaths().size());
    }
}
