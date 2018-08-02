package ynca.nfs.Models;

import java.util.HashMap;

/**
 * Created by bolee on 8.4.17..
 */

public class Servis {




    public String getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }



    public HashMap<String, Zahtev> getZahtevi() {
        return zahtevi;
    }

    public void setZahtevi(HashMap<String, Zahtev> zahtevi) {
        this.zahtevi = zahtevi;
    }
    
    //// TODO: 6/2/2017  float prosecnaOcena;
    private HashMap<String, Zahtev> zahtevi;
    private String naziv;
    private String imeVlasnika;
    //private int id;
    private String brojTelefona;
    private String email;
    private String adresa;
    private double longi;
    private double lat;
    private HashMap<String, Automobil> automobili;
    private HashMap<String ,Poruka> primljenePoruke;
    private String UID;
    private HashMap<String, Usluga> usluge;

    public HashMap<String, Usluga> getUsluge() {
        return usluge;
    }

    public void setUsluge(HashMap<String, Usluga> usluge) {
        this.usluge = usluge;
    }



    public HashMap<String, Recenzija> getRecenzije() {
        return recenzije;
    }

    public void setRecenzije(HashMap<String, Recenzija> recenzije) {
        this.recenzije = recenzije;
    }

    HashMap<String, Recenzija> recenzije;

    public HashMap<String, Poruka> getPrimljenePoruke() {
        return primljenePoruke;
    }

    public void setPrimljenePoruke(HashMap<String, Poruka> primljenePoruke) {
        this.primljenePoruke = primljenePoruke;
    }



    public HashMap<String, Automobil> getAutomobili() {
        return automobili;
    }

    public void setAutomobili(HashMap<String, Automobil> automobili) {
        this.automobili = automobili;
    }



    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }



    public Servis(String _ime, String _prezime, String _adresa, String _brojTelefona, String _email, double longi, double lat)
    {
        naziv=_ime;
        imeVlasnika=_prezime;
        adresa = _adresa;
        //id=_id;
        brojTelefona=_brojTelefona;
        email=_email;
        this.longi = longi;
        this.lat = lat;
        usluge = new HashMap<>();
        zahtevi = new HashMap<>();
        primljenePoruke = new HashMap<>();
        automobili = new HashMap<>();
    }

    public Servis(String _ime, String _prezime, String _adresa, String _brojTelefona, String _email, String uid)
    {
        naziv=_ime;
        imeVlasnika=_prezime;
        adresa = _adresa;
        //id=_id;
        brojTelefona=_brojTelefona;
        email=_email;
        UID = uid;
        zahtevi = new HashMap<>();
        usluge = new HashMap<>();
        primljenePoruke = new HashMap<>();

    }


    public Servis(){}

    public String getNaziv(){
        return this.naziv;
    }

    public void setNaziv(String naziv){
        this.naziv = naziv;
    }

    public String getImeVlasnika(){
        return this.imeVlasnika;
    }

    public void setImeVlasnika(String s){
        this.imeVlasnika = s;
    }

    public String getTelefon(){
        return this.brojTelefona;
    }

    public void setTelefon(String s){
        this.brojTelefona = s;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String s){
        this.email = s;
    }

    public String getAdresa(){
        return this.adresa;
    }

    public void setAdresa(String s){
        this.adresa = s;
    }

    private void zakaziServis()
    {
    }

    public void dodajUslugu(Usluga usluga, String key)
    {
        usluge.put(key,usluga);
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
}
