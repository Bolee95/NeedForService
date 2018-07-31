package ynca.nfs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bolee on 8.4.17..
 */

public class Recenzija {

    //// TODO: 6/2/2017  treba da se ubaci u nazu string korisnkik
    private String korisnik;
    private String komentar;
    private float ocena;

    public Recenzija(String korisnik, String komentar, float ocena) {
        this.korisnik = korisnik;
        this.komentar = komentar;
        this.ocena = ocena;
    }

    public Recenzija(String korisnik) {
        this.korisnik = korisnik;

    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }



    public float getOcena() {
        return ocena;
    }

    public void setOcena(float ocena) {
        this.ocena = ocena;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }



    public Recenzija()
    {}

    public void dodajOcenu(int ocena)
    {

    }
    public void dodajKomentar(String komentar)
    {

    }
}
