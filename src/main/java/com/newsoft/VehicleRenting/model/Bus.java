package main.java.com.newsoft.VehicleRenting.model;


import java.util.Objects;

/**
 * Bus class extending Vehicle.
 * Keeps your original attributes: capacity and model.
 */
public class Bus extends Vehicle {

    private int capacity;
    private String model;

    /**
     * Full constructor.
     *
     * @param capacity seating capacity (must be >= 0)
     * @param model model name (must not be null/blank)
     * @param registerNumber registration number (passed to Vehicle superclass)
     * @param color bus colour (passed to Vehicle superclass)
     */
    public Bus(int capacity, String model, String registerNumber, String color) {
        super(registerNumber, color);  // Calls Vehicle constructor
        setCapacity(capacity);
        setModel(model);
    }

    /**
     * Convenience constructor when you only know register + color.
     */
    public Bus(String registerNumber, String color) {
        super(registerNumber, color);
        this.capacity = 0;
        this.model = "";
    }

    // Validation helper
    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    /* ---------- Getters & Setters ---------- */

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be >= 0");
        }
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = requireNonBlank(model, "model");
    }

    /* ---------- Display & Utility ---------- */

    public void displayInfo() {
        System.out.println(this.toString());
        System.out.println("Model: " + model + ", Capacity: " + capacity);
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
        return String.format("Bus[reg=%s, model=%s, capacity=%d, color=%s, photos=%d]",
                getRegisterNumber(), model, capacity, getColor(), getPhotoPaths().size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bus)) return false;
        if (!super.equals(o)) return false;
        Bus bus = (Bus) o;
        return capacity == bus.capacity && Objects.equals(model, bus.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), capacity, model);
    }
}
