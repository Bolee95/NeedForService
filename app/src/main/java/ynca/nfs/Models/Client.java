package ynca.nfs.Models;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;


public class Client {


    private HashMap<String, Vehicle> listOfCars; //lista vozila koja klijent poseduje
    private HashMap<String, Request> requests; //lista zahteva za servisiranjem koje je uputnio


    private String firstName; //ime
    private String lastName; //prezime
    private String phoneNumber; //broj telefona
    private String email; //email
    private String UID; //kljuc u Firebaseu
    private double lastKnownLat; //poslednja poznata lat koordinata, treba cesto da se azurira
    private double lastKnownlongi; //poslednja poznata longi kooridnata
    private HashMap<String,String > listOfFriendsUIDs; //lista prijatelja
    private HashMap<String,VehicleService> listOfAddedServices; //lista servisa koje je dodao korisnik
    private int reviewsCount; //broji koliko puta je ocenjivao servise
    private int servicesAdded; //brojac koji sadrzi broj dodatih servisa --> Za rangiranje, da bi moglo odmah order da ide preko firebasea



    public Client(String _ime, String _prezime, String _brojTelefona, String _email, String uid)
    {

        firstName=_ime;
        lastName=_prezime;
        phoneNumber=_brojTelefona;
        email=_email;
        listOfCars = new HashMap<String, Vehicle>();
        setListOfFriendsUIDs(new HashMap<String, String>());
        UID = uid;
        requests = new HashMap<String, Request>();
        setReviewsCount(0);
        setServicesAdded(0);
    }



    public Client(){}


    public HashMap<String, Request> getZahtevi() {
        return requests;
    }

    public void setZahtevi(HashMap<String, Request> zahtevi) {
        this.requests = zahtevi;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }


    public HashMap<String, Vehicle> getListOfCars() {
        return listOfCars;
    }

    public void setListOfCars(HashMap<String, Vehicle> listOfCars) {
        this.listOfCars = listOfCars;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public  int getPoints() { return  reviewsCount*2 + servicesAdded*3; }


    public void dodajVozilo(String key, Vehicle temp)
    {
        listOfCars.put(key, temp);
    }

    public void ukloniVozilo(Vehicle temp)
    {
        listOfCars.remove(temp);
    }

    public double getLastKnownLat() {
        return lastKnownLat;
    }

    public void setLastKnownLat(double lastKnownLat) {
        this.lastKnownLat = lastKnownLat;
    }

    public double getLastKnownlongi() {
        return lastKnownlongi;
    }

    public void setLastKnownlongi(double lastKnownlongi) {
        this.lastKnownlongi = lastKnownlongi;
    }


    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }


    public int getServicesAdded() {
        return servicesAdded;
    }

    public void setServicesAdded(int servicesAdded) {
        this.servicesAdded = servicesAdded;
    }


    public HashMap<String, String> getListOfFriendsUIDs() {
        return listOfFriendsUIDs;
    }

    public void setListOfFriendsUIDs(HashMap<String, String> listOfFriendsUIDs) {
        this.listOfFriendsUIDs = listOfFriendsUIDs;
    }

    public HashMap<String, VehicleService> getListOfAddedServices() {
        return listOfAddedServices;
    }

    public void setListOfAddedServices(HashMap<String, VehicleService> listOfAddedServices) {
        this.listOfAddedServices = listOfAddedServices;
    }
}
