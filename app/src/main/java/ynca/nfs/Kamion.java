package ynca.nfs;

/**
 * Created by bolee on 7.4.17..
 */

public class Kamion  {

    public float getNosivost() {
        return nosivost;
    }

    public void setNosivost(float nosivost) {
        this.nosivost = nosivost;
    }

    private float nosivost;
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

    public void dodajPodatke(String reg, String _model, String _proizvodjac, int _predjeniPut, float _nosivost)
    {
        nosivost=_nosivost;
        regBroj = reg;
        model = _model;
        proizvodjac = _proizvodjac;
        predjeniPut = _predjeniPut;

    }

    public Kamion(){}

}
