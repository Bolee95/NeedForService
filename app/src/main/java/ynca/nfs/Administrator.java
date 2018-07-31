package ynca.nfs;

/**
 * Created by bolee on 8.4.17..
 */

public class Administrator {

    private String ime;
    private String prezime;
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(int brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private int brojTelefona;
    private String email;

    public Administrator(String _ime,String _prezime, int _id,int _brojTelefona,String _email)
    {
        ime=_ime;
        prezime=_prezime;
        id=_id;
        brojTelefona=_brojTelefona;
        email=_email;

    }

    public Administrator(){}

    public void odobriServis()
    {
    }

    public void ukloniKorisnika()
    {
    }

}
