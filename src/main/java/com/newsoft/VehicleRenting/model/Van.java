package main.java.com.newsoft.VehicleRenting.model;
//import java.util.Objects;

public class Van extends Vehicle {

    private String model;
    private int seatingCapacity;

    /**
     * Full constructor (original).
     */
    public Van(String model, String registerNumber, String color, int seatingCapacity) {
        super(requireNonBlank(registerNumber, "registerNumber"),
              requireNonBlank(color, "color"));
        this.model = requireNonBlank(model, "model");
        this.seatingCapacity = seatingCapacity;
    }

    /**
     * Simple constructor used when reading from CSV.
     */
    public Van(String registerNumber, String color) {
        super(requireNonBlank(registerNumber, "registerNumber"),
              requireNonBlank(color, "color"));
        this.model = "Unknown";
        this.seatingCapacity = 0;
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    public String getModel() { return model; }
    public int getSeatingCapacity() { return seatingCapacity; }

    public void displayInfo() {
        System.out.println(this.toString());
        System.out.println("Model: " + model + ", Seating Capacity: " + seatingCapacity);
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
        return String.format("Van[reg=%s, model=%s, seats=%d, color=%s, photos=%d]",
                getRegisterNumber(), model, seatingCapacity, getColor(), getPhotoPaths().size());
    }
}
