package ynca.nfs.Models;

public class Review {

    private String user;
    private String comment;
    private float rate;

    public Review(String korisnik, String komentar, float ocena) {
        this.setUser(korisnik);
        this.setComment(komentar);
        this.setRate(ocena);
    }

    public Review(String korisnik) {
        this.setUser(korisnik);

    }

    public Review()
    {}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
