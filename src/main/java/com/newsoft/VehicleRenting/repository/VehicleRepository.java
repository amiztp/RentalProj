package main.java.com.newsoft.VehicleRenting.repository;

import main.java.com.newsoft.VehicleRenting.model.Bike;
import main.java.com.newsoft.VehicleRenting.model.Bus;
import main.java.com.newsoft.VehicleRenting.model.Car;
import main.java.com.newsoft.VehicleRenting.model.Lorry;
import main.java.com.newsoft.VehicleRenting.model.Van;
import main.java.com.newsoft.VehicleRenting.model.Vehicle;

import java.io.*;
import java.util.*;


public class VehicleRepository {

    private static final String FILE_PATH = "Data/vehicles.csv";

    /**
     * Load all vehicles from CSV into a List<Vehicle>.
     */
    public List<Vehicle> loadAllVehicles() {

        List<Vehicle> vehicles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line = br.readLine(); // skip header line

            while ((line = br.readLine()) != null) {

                // Split safely (include empty columns)
                String[] parts = line.split(",", -1);

                if (parts.length < 3) continue;

                String reg = parts[0].trim();
                String color = parts[1].trim();
                String type = parts[2].trim();

                String model = parts.length > 3 ? parts[3].trim() : "";
                String numberOfDoors = parts.length > 4 ? parts[4].trim() : "";
                String seatingCapacity = parts.length > 5 ? parts[5].trim() : "";
                String loadCapacity = parts.length > 6 ? parts[6].trim() : "";
                String photoPathString = parts.length > 7 ? parts[7].trim() : "";

                Vehicle vehicle;

                switch (type) {
                    case "Car":
                        if (!numberOfDoors.isEmpty() && !model.isEmpty()) {
                            vehicle = new Car(Integer.parseInt(numberOfDoors), model, reg, color);
                        } else {
                            vehicle = new Car(reg, color);
                        }
                        break;

                    case "Van":
                        if (!seatingCapacity.isEmpty() && !model.isEmpty()) {
                            vehicle = new Van(model, reg, color, Integer.parseInt(seatingCapacity));
                        } else {
                            vehicle = new Van(reg, color);
                        }
                        break;

                    case "Bike":
                        if (!model.isEmpty()) {
                            vehicle = new Bike(model, reg, color);
                        } else {
                            vehicle = new Bike(reg, color);
                        }
                        break;

                    case "Lorry":
                        if (!loadCapacity.isEmpty() && !model.isEmpty()) {
                            vehicle = new Lorry(model, reg, color, Integer.parseInt(loadCapacity));
                        } else {
                            vehicle = new Lorry(reg, color);
                        }
                        break;

                    case "Bus":
                        if (!seatingCapacity.isEmpty() && !model.isEmpty()) {
                            vehicle = new Bus(Integer.parseInt(seatingCapacity), model, reg, color);
                        } else {
                            vehicle = new Bus(reg, color);
                        }
                        break;

                    default:
                        System.out.println("Unknown vehicle type: " + type);
                        continue;
                }

                // Load photo paths
                if (!photoPathString.isEmpty()) {
                    String[] photoPaths = photoPathString.split("\\|");
                    for (String p : photoPaths) {
                        vehicle.addPhotoPath(p.trim());
                    }
                }

                // Load description (column 9) if available
                if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                    // Unescape newlines from CSV
                    String desc = parts[9].trim().replace("\\n", "\n");
                    vehicle.setDescription(desc);
                }

                vehicles.add(vehicle);
            }

        } catch (IOException e) {
            System.out.println("Error reading vehicles.csv: " + e.getMessage());
        }

        return vehicles;
    }

    /**
     * Find vehicle by registration number.
     */
    public Vehicle findByRegisterNumber(List<Vehicle> list, String regNumber) {
        for (Vehicle v : list) {
            if (v.getRegisterNumber().equalsIgnoreCase(regNumber)) {
                return v;
            }
        }
        return null;
    }
}
