package ynca.nfs;

/**
 * Created by Nemanja Djordjevic on 5/29/2017.
 */

public class Zahtev {

    private String tipUsluge;
    private Automobil automobil;
    private String predlozeniTermin;
    private String napomena;
    private String idServisa;
    private String id;
    private String idKlijenta;
    private String imeKlijenta;

    public String getImeKlijenta() {
        return imeKlijenta;
    }

    public void setImeKlijenta(String imeServisa) {
        this.imeKlijenta = imeServisa;
    }




    public String getIdServisa() {
        return idServisa;
    }

    public void setIdServisa(String idServisa) {
        this.idServisa = idServisa;
    }


    public String getIdKlijenta() {
        return idKlijenta;
    }

    public void setIdKlijenta(String idKlijenta) {
        this.idKlijenta = idKlijenta;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public String getTipUsluge() {
        return tipUsluge;
    }

    public void setTipUsluge(String tipUsluge) {
        this.tipUsluge = tipUsluge;
    }

    public Automobil getAutomobil() {
        return automobil;
    }

    public void setAutomobil(Automobil automobil) {
        this.automobil = automobil;
    }

    public String getPredlozeniTermin() {
        return predlozeniTermin;
    }

    public void setPredlozeniTermin(String predlozeniTermin) {
        this.predlozeniTermin = predlozeniTermin;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }



    public Zahtev(String t, String p, String n, Automobil a, String s,String kID, String imeServisa){

        tipUsluge = t;
        automobil = a;
        predlozeniTermin = p;
        napomena = n;
        idServisa = s;
        idKlijenta = kID;
        this.imeKlijenta = imeServisa;

    }
    public Zahtev(){}
}
