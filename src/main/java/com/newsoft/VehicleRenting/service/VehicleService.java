package main.java.com.newsoft.VehicleRenting.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import main.java.com.newsoft.VehicleRenting.repository.VehicleRepository;
import main.java.com.newsoft.VehicleRenting.model.Vehicle;

public class VehicleService {

    private final VehicleRepository repository;
    private List<Vehicle> vehicles;

    public VehicleService() {
        this.repository = new VehicleRepository();
        this.vehicles = repository.loadAllVehicles();   // load vehicles when service starts
    }

    /**
     * Returns all vehicles loaded from CSV.
     */
    public List<Vehicle> getAllVehicles() {
        return vehicles;
    }

    /**
     * Search vehicle by registration number.
     */
    public Vehicle getVehicle(String regNumber) {
        return repository.findByRegisterNumber(vehicles, regNumber);
    }

    /**
     * Adds a photo to a specific vehicle.
     * Copies the file to /data/photos folder and saves the relative path.
     */
    public boolean addPhotoToVehicle(String regNumber, String sourceImagePath) {
        Vehicle vehicle = getVehicle(regNumber);

        if (vehicle == null) {
            System.out.println("Vehicle not found!");
            return false;
        }

        File source = new File(sourceImagePath);
        if (!source.exists()) {
            System.out.println("Image file not found!");
            return false;
        }

        // create photos folder if missing
        File destFolder = new File("data/photos");
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        // generate unique file name
        String extension = getFileExtension(source.getName());
        String newFileName = regNumber + "-" + System.currentTimeMillis() + "." + extension;

        File destFile = new File(destFolder, newFileName);

        try {
            Files.copy(source.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Failed to copy image: " + e.getMessage());
            return false;
        }

        // save relative path in the vehicle
        String relativePath = "photos/" + newFileName;
        vehicle.addPhotoPath(relativePath);

        System.out.println("Photo added successfully: " + relativePath);
        return true;
    }

    /**
     * Extract file extension (jpg, png, etc.)
     */
    private String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? "" : fileName.substring(dot + 1);
    }
}
