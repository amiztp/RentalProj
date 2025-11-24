package main.java.com.newsoft.VehicleRenting;

import main.java.com.newsoft.VehicleRenting.service.VehicleService;
import main.java.com.newsoft.VehicleRenting.model.Vehicle;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Vehicle Renting System ===\n");
        
        // Initialize the vehicle service
        VehicleService vehicleService = new VehicleService();
        
        // Get all vehicles
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        
        System.out.println("Total vehicles: " + vehicles.size());
        System.out.println("\nVehicle Details:");
        System.out.println("-".repeat(50));
        
        // Display all vehicles
        for (Vehicle vehicle : vehicles) {
            vehicle.displayInfo();
            System.out.println("-".repeat(50));
        }
        
        // Example: Search for a specific vehicle
        if (!vehicles.isEmpty()) {
            Vehicle firstVehicle = vehicles.get(0);
            System.out.println("\nSearching for vehicle: " + firstVehicle.getRegisterNumber());
            Vehicle found = vehicleService.getVehicle(firstVehicle.getRegisterNumber());
            if (found != null) {
                System.out.println("Found: " + found);
            }
        }
        
        System.out.println("\n=== Program completed successfully ===");
    }
}
