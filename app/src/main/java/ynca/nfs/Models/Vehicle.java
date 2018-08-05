package ynca.nfs.Models;

import java.io.Serializable;
import java.util.List;

public class Vehicle implements Serializable{

    private String vehicleType;  //kamion, automobil...
    private int chassisNumber; //broj sasije
    private String fuelType;  //tip goriva
    private int yearOfProduction; //godina proizvodnje
    private String dateOfLastService; //poslednji servis
    private String registyNumber; //registracioni broj
    private String model;  //model vozila
    private String manufacturer;  //proizvodjac
    private int mileage; //kilometraza
    private String vehicleID; //id vozila
    private String ownerID; //id vlasnika
    private String typeOfService; //Ovo je iz starog koda, treba da se proveri za sta je
    private String ownersMail; //email vlasnika
    private boolean onService; //STATUS VOZILA, DA LI se trenutno servisira ili ne

    public Vehicle(String reg, String _model, String _proizvodjac, int _predjeniPut,
                   int nbrojSasije, String ntipGodiva, int ngodinaProizvodnje, String nposlednjiSer,
                   String vlasnik, String vlasnikMail, String usluga )
    {

        chassisNumber = nbrojSasije;
        fuelType = ntipGodiva;
        yearOfProduction = ngodinaProizvodnje;
        dateOfLastService = nposlednjiSer;
        registyNumber = reg;
        model = _model;
        manufacturer = _proizvodjac;
        mileage = _predjeniPut;
        vehicleID = "test";
        ownerID = vlasnik;
        ownersMail = vlasnikMail;
        typeOfService = usluga;
        onService = false;
    }
    public Vehicle(){}



    public String getOwnersMail() {
        return ownersMail;
    }

    public void setOwnersMail(String ownersMail) {
        this.ownersMail = ownersMail;
    }


    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }


    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public int getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(int chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(int yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public String getDateOfLastService() {
        return dateOfLastService;
    }

    public void setDateOfLastService(String dateOfLastService) {
        this.dateOfLastService = dateOfLastService;
    }


    public String getRegistyNumber() {
        return registyNumber;
    }

    public void setRegistyNumber(String registyNumber) {
        this.registyNumber = registyNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int predjeniPut) {
        this.mileage = mileage;
    }


    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getTypeOfService() {
        return typeOfService;
    }

    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }


    public boolean isOnService() {
        return onService;
    }

    public void setOnService(boolean onService) {
        this.onService = onService;
    }
}
