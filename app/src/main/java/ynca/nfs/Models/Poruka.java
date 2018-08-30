package ynca.nfs.Models;

/**
 * Created by Nikola on 5/26/2017.
 */
import java.io.Serializable;
@SuppressWarnings("serial") //da ne prikazuje warnnings kad kompajlira
public class Poruka implements Serializable {

    private boolean procitana;
    private String tekst;



    private String posiljalac;
    private String primaoc;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getPosiljalac() {
        return posiljalac;
    }

    public void setPosiljalac(String posiljalac) {
        this.posiljalac = posiljalac;
    }

    public String getPrimaoc() {
        return primaoc;
    }

    public void setPrimaoc(String primaoc) {
        this.primaoc = primaoc;
    }

    private String naslov;



    public boolean isProcitana() {
        return procitana;
    }

    public void setProcitana(boolean procitana) {
        this.procitana = procitana;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }



    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }


    public Poruka(){}

    public Poruka (boolean f, String src, String
            dst, String title, String txt, String ID)
    {
        procitana=f;
        posiljalac = src;
        primaoc = dst;
        naslov=title;
        tekst=txt;
        this.id = ID;
    }

    public void oznaciKaoProcitanu() { procitana = true;}

}
