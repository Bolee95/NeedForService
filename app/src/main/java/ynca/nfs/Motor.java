package ynca.nfs;

/**
 * Created by bolee on 7.4.17..
 */

public class Motor  {
    public int getBrojTockova() {
        return brojTockova;
    }

    public void setBrojTockova(int brojTockova) {
        this.brojTockova = brojTockova;
    }

    private int brojTockova;
    private String regBroj;
    private String model;

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

    private String proizvodjac;
    private int predjeniPut;

    public Motor(){}

    public Motor(String reg, String _model, String _proizvodjac, int _predjeniPut, int _brojTockova)
    {
        regBroj = reg;
        model = _model;
        brojTockova =_brojTockova;
        proizvodjac = _proizvodjac;
        predjeniPut = _predjeniPut;
    }
}
