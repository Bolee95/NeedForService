package ynca.nfs;

/**
 * Created by bolee on 8.4.17..
 */

public class Usluga {

    public String getUsluga() {
        return usluga;
    }

    public Usluga(String usluga, int cena, String idUsluge) {
        this.usluga = usluga;
        this.cena = cena;
        this.idUsluge = idUsluge;
    }

    public void setUsluga(String usluga) {
        this.usluga = usluga;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    private String usluga;
    private int cena;

    public String getIdUsluge() {
        return idUsluge;
    }

    public void setIdUsluge(String idUsluge) {
        this.idUsluge = idUsluge;
    }

    private String idUsluge;

    public Usluga(){}



}
