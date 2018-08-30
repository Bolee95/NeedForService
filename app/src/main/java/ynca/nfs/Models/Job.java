package ynca.nfs.Models;

public class Job {

    private String jobID;
    private String job;
    private int price;

    public Job(String usluga, int cena, String idUsluge) {
        this.setJob(usluga);
        this.setPrice(cena);
        this.setJobID(idUsluge);
    }

    public Job(){}

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
