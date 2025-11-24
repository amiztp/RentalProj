package main.java.com.newsoft.VehicleRenting.model;



import java.util.Objects;

/**
 * Car class that extends Vehicle.
 * Keeps existing attributes: numberOfDoors and model.
 */
public class Car extends Vehicle {

    private int numberOfDoors;
    private String model;

    /**
     * Full constructor.
     *
     * @param numberOfDoors number of doors (must be >= 0)
     * @param model model name (non-null, non-blank)
     * @param registerNumber vehicle registration number (passed to Vehicle)
     * @param color vehicle color (passed to Vehicle)
     */
    public Car(int numberOfDoors, String model, String registerNumber, String color) {
        super(registerNumber, color); // call Vehicle constructor (validates registerNumber & color)
        setNumberOfDoors(numberOfDoors);
        setModel(model);
    }

    /**
     * Convenience constructor when you prefer to supply register & color later.
     */
    public Car(String registerNumber, String color) {
        super(registerNumber, color);
        this.numberOfDoors = 0;
        this.model = "";
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(int numberOfDoors) {
        if (numberOfDoors < 0) {
            throw new IllegalArgumentException("numberOfDoors must be >= 0");
        }
        this.numberOfDoors = numberOfDoors;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = requireNonBlank(model, "model");
    }

    /**
     * Convenient display used by CLI.
     */
    public void displayInfo() {
        System.out.println(this.toString());
        System.out.println("Model: " + model + ", Doors: " + numberOfDoors);
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
        return String.format("Car[reg=%s, model=%s, doors=%d, color=%s, photos=%d]",
                getRegisterNumber(), model, numberOfDoors, getColor(), getPhotoPaths().size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        if (!super.equals(o)) return false;
        Car car = (Car) o;
        return numberOfDoors == car.numberOfDoors && Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numberOfDoors, model);
    }
}
