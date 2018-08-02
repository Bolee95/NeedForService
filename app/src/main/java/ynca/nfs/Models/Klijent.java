package ynca.nfs.Models;

import java.util.HashMap;

/**
 * Created by bolee on 8.4.17..
 */

public class Klijent {

    private HashMap<String, Automobil> listOfCars;
    private HashMap<String, Zahtev> zahtevi;



    private HashMap<String, Poruka> primljenePoruke;
    private String ime;
    private String prezime;
    //private int id;
    private String brojTelefona;
    private String email;
    private String UID;






    public HashMap<String, Zahtev> getZahtevi() {
        return zahtevi;
    }

    public void setZahtevi(HashMap<String, Zahtev> zahtevi) {
        this.zahtevi = zahtevi;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public HashMap<String, Poruka> getPrimljenePoruke() {
        return primljenePoruke;
    }

    public void setPrimljenePoruke(HashMap<String, Poruka> primljenePoruke) {
        this.primljenePoruke = primljenePoruke;
    }

    public HashMap<String, Automobil> getListOfCars() {
        return listOfCars;
    }

    public void setListOfCars(HashMap<String, Automobil> listOfCars) {
        this.listOfCars = listOfCars;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public String getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public Klijent(String _ime, String _prezime, String _brojTelefona, String _email, String uid)
    {

        ime=_ime;
        prezime=_prezime;
        //id=_id;
        brojTelefona=_brojTelefona;
        email=_email;
        listOfCars = new HashMap<String, Automobil>();
        UID = uid;
        zahtevi = new HashMap<>();
        primljenePoruke = new HashMap<>();
    }

    public Klijent(){}

    public void dodajVozilo(String key, Automobil temp)
    {
        listOfCars.put(key, temp);
    }

    public void ukloniVozilo(Automobil temp)
    {
        listOfCars.remove(temp);
    }

    private void zakaziServis()
    {}

    public void postaviPitanje(String pitanje)
    {}


}
