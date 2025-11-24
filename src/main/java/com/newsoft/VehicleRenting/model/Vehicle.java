package main.java.com.newsoft.VehicleRenting.model;




import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle model with minimal fields (registerNumber, color) and photo support.
 * No new domain attributes were added other than the photo path list.
 */
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    private String registerNumber;
    private String color;

    private final List<String> photoPaths = new ArrayList<>();

    /**
     * Construct a Vehicle. registerNumber and color must not be null or blank.
     *
     * @param registerNumber vehicle registration number (unique identifier)
     * @param color the vehicle colour
     * @throws IllegalArgumentException if a parameter is null/blank
     */
    public Vehicle(String registerNumber, String color) {
        this.registerNumber = requireNonBlank(registerNumber, "registerNumber");
        this.color = requireNonBlank(color, "color");
    }

    // Helper validator
    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    /* ----------------- Getters & Setters ----------------- */

    public String getRegisterNumber() {
        return registerNumber;
    }

    /**
     * Set a new register number. Must be non-null and non-blank.
     * Use with caution: changing the id used for equality can be problematic.
     * Prefer creating a new Vehicle for permanent identity changes.
     */
    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = requireNonBlank(registerNumber, "registerNumber");
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = requireNonBlank(color, "color");
    }

    /* ----------------- Photo methods ----------------- */

    /**
     * Add a photo path for this vehicle. The path should be a relative path
     * pointing to where the file will live inside your project (e.g. "photos/ABC-123-img1.jpg").
     * Duplicate paths are allowed but you may check for duplicates before adding if desired.
     *
     * @param relativePath relative path to the image file
     */
    public void addPhotoPath(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        this.photoPaths.add(relativePath);
    }

    /**
     * Remove a photo path previously added.
     *
     * @param relativePath the relative path to remove
     * @return true if removed, false otherwise
     */
    public boolean removePhotoPath(String relativePath) {
        return this.photoPaths.remove(relativePath);
    }

    /**
     * Returns an unmodifiable view of the photo paths.
     *
     * @return list of photo relative paths
     */
    public List<String> getPhotoPaths() {
        return Collections.unmodifiableList(photoPaths);
    }

    /* ----------------- Utility overrides ----------------- */

    @Override
    public String toString() {
        return String.format("Vehicle[reg=%s, color=%s, photos=%d]", registerNumber, color, photoPaths.size());
    }

    /**
     * Equality is based on registerNumber (assumes registration number uniquely identifies a vehicle).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(registerNumber, vehicle.registerNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registerNumber);
    }

    public void displayInfo() {
        System.out.println(this.toString());
        if (!photoPaths.isEmpty()) {
            System.out.println("Photos:");
            for (String p : photoPaths) {
                System.out.println("  - " + p);
            }
        } else {
            System.out.println("No photos available.");
        }
    }
}
