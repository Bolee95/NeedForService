package ynca.nfs.Models;

import java.util.HashMap;

public class VehicleService {

    private HashMap<String, Request> requests; //lista zahteva
    private String name; //? //naziv servisa
    private String ownersName; //ime vlasnika
    //private int id;
    private String phoneNumber; //broj telefona
    private String email; //email
    private String address; //adresa
    private String city;
    private double longi; //longitude koordinata
    private double lat; //latitude koordinalta
    private HashMap<String, Vehicle> acceptedServices; //lista vozila koja su trenutno na servisu
    private HashMap<String ,Poruka> primljenePoruke;
    private String UID;
    private HashMap<String, Job> services; //lista usluga koje servis nudi
    private HashMap<String, Review> reviews; //recenzije
    private Boolean addedByUser;



    public VehicleService(String _ime, String _prezime, String _adresa, String _brojTelefona, String _email, double longi, double lat)
    {
        name=_ime;
        setOwnersName(_prezime);
        setAddress(_adresa);
        //id=_id;
        setPhoneNumber(_brojTelefona);
        setEmail(_email);
        this.setLongi(longi);
        this.setLat(lat);
        services = new HashMap<String, Job>();
        setRequests(new HashMap<String,Request>());
        setPrimljenePoruke(new HashMap<String,Poruka>());
        setAcceptedServices(new HashMap<String,Vehicle>());
        setReviews(new HashMap<String, Review>());
        addedByUser = false;
    }

    public VehicleService(String _ime, String _prezime, String _adresa, String _brojTelefona, String _email, String uid)
    {
        name=_ime;
        setOwnersName(_prezime);
        setAddress(_adresa);
        //id=_id;
        setPhoneNumber(_brojTelefona);
        setEmail(_email);
        setUID(uid);
        setRequests(new HashMap<String,Request>());
        services = new HashMap<String, Job>();
        setPrimljenePoruke(new HashMap<String,Poruka>());
        setReviews(new HashMap<String, Review>());

    }


    public VehicleService(){}


    public HashMap<String, Request> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<String, Request> requests) {
        this.requests = requests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnersName() {
        return ownersName;
    }

    public void setOwnersName(String ownersName) {
        this.ownersName = ownersName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public HashMap<String, Vehicle> getAcceptedServices() {
        return acceptedServices;
    }

    public void setAcceptedServices(HashMap<String, Vehicle> acceptedServices) {
        this.acceptedServices = acceptedServices;
    }

    public HashMap<String, Poruka> getPrimljenePoruke() {
        return primljenePoruke;
    }

    public void setPrimljenePoruke(HashMap<String, Poruka> primljenePoruke) {
        this.primljenePoruke = primljenePoruke;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public HashMap<String, Job> getServices() {
        return services;
    }

    public void setServices(HashMap<String, Job> services) {
        this.services = services;
    }

    public HashMap<String, Review> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, Review> reviews) {
        this.reviews = reviews;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getAddedByUser() {
        return addedByUser;
    }

    public void setAddedByUser(Boolean addedByUser) {
        this.addedByUser = addedByUser;
    }
}
