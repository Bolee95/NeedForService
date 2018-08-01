package ynca.nfs.Models;

import java.io.Serializable;

/**
 * Created by bolee on 7.4.17..
 */

public class Automobil implements Serializable{


    private int brojSasije;
    private String tipGoriva;
    private int godinaProizvodnje;
    private String poslednjiServisDatum;
    private String regBroj;
    private String model;
    private String proizvodjac;
    private int predjeniPut;
    private String voziloID;
    private String vlasnikID;
    private String tipUsluge;
    private String vlasnikMail;


    public String getVlasnikMail() {
        return vlasnikMail;
    }

    public void setVlasnikMail(String vlasnikMail) {
        this.vlasnikMail = vlasnikMail;
    }


    public String getVlasnikID() {
        return vlasnikID;
    }

    public void setVlasnikID(String vlasnikID) {
        this.vlasnikID = vlasnikID;
    }


    public String getVoziloID() {
        return voziloID;
    }

    public void setVoziloID(String voziloID) {
        this.voziloID = voziloID;
    }

    public int getBrojSasije() {
        return brojSasije;
    }

    public void setBrojSasije(int brojSasije) {
        this.brojSasije = brojSasije;
    }

    public String getTipGoriva() {
        return tipGoriva;
    }

    public void setTipGoriva(String tipGoriva) {
        this.tipGoriva = tipGoriva;
    }

    public int getGodinaProizvodnje() {
        return godinaProizvodnje;
    }

    public void setGodinaProizvodnje(int godinaProizvodnje) {
        this.godinaProizvodnje = godinaProizvodnje;
    }

    public String getPoslednjiServisDatum() {
        return poslednjiServisDatum;
    }

    public void setPoslednjiServisDatum(String poslednjiServisDatum) {
        this.poslednjiServisDatum = poslednjiServisDatum;
    }


    public String getRegBroj() {
        return regBroj;
    }

    public void setRegBroj(String regBroj) {
        this.regBroj = regBroj;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProizvodjac() {
        return proizvodjac;
    }

    public void setProizvodjac(String proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    public int getPredjeniPut() {
        return predjeniPut;
    }

    public void setPredjeniPut(int predjeniPut) {
        this.predjeniPut = predjeniPut;
    }

    public String getTipUsluge() {
        return tipUsluge;
    }

    public void setTipUsluge(String tipUsluge) {
        this.tipUsluge = tipUsluge;
    }

    public Automobil(String reg, String _model, String _proizvodjac, int _predjeniPut,
                     int nbrojSasije, String ntipGodiva, int ngodinaProizvodnje, String nposlednjiSer,
                     String vlasnik, String vlasnikMail, String usluga )
    {

        brojSasije = nbrojSasije;
        tipGoriva = ntipGodiva;
        godinaProizvodnje = ngodinaProizvodnje;
        poslednjiServisDatum = nposlednjiSer;
        regBroj = reg;
        model = _model;
        proizvodjac = _proizvodjac;
        predjeniPut = _predjeniPut;
        voziloID = "test";
        vlasnikID = vlasnik;
        this.vlasnikMail = vlasnikMail;
        this.tipUsluge = usluga;
    }
    public Automobil(){}


}
