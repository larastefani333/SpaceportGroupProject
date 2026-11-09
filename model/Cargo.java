package model;

public class Cargo {
    
    private final String cargoID;
    private final int cargoUnits;

    public Cargo (String cargoID, int cargoUnits) {
        this.cargoID = cargoID;
        this.cargoUnits = cargoUnits;
    }

    public String getCargoID() {
        return cargoID;
    }

    public int getCargoUnits() {
        return cargoUnits;
    }
}
