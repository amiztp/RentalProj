package main.java.com.newsoft.VehicleRenting.model;



public class Lorry extends Vehicle {

    private String model;
    private int loadCapacity;

    /**
     * Full constructor.
     */
    public Lorry(String model, String registerNumber, String color, int loadCapacity) {
        super(requireNonBlank(registerNumber, "registerNumber"),
              requireNonBlank(color, "color"));
        this.model = requireNonBlank(model, "model");
        this.loadCapacity = loadCapacity;
    }

    /**
     * Simple constructor (CSV loading).
     */
    public Lorry(String registerNumber, String color) {
        super(requireNonBlank(registerNumber, "registerNumber"),
              requireNonBlank(color, "color"));
        this.model = "Unknown";
        this.loadCapacity = 0;
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    public String getModel() { return model; }
    public int getLoadCapacity() { return loadCapacity; }

    public void displayInfo() {
        System.out.println(this.toString());
        System.out.println("Model: " + model + ", Load Capacity: " + loadCapacity);
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
        return String.format("Lorry[reg=%s, model=%s, capacity=%d, color=%s, photos=%d]",
                getRegisterNumber(), model, loadCapacity, getColor(), getPhotoPaths().size());
    }
}
