package ynca.nfs.Models;

import java.sql.Time;

/**
 * Created by Nemanja Djordjevic on 5/29/2017.
 */

public class Request {

    private String typeOfService;
    private Vehicle vehicle;
    private String proposedDate; //TODO: Promeniti u DateTime i promeniti elemente za unos
    private String proposedTime;
    private String note;
    private String serviceId;
    private String id;
    private String clientId;
    private String clientName;



    public Request(String t, String p, String n, Vehicle a, String s, String kID, String imeServisa){

        setTypeOfService(t);
        setVehicle(a);
        setProposedDate(p);
        setNote(n);
        setServiceId(s);
        setClientId(kID);
        this.setClientName(imeServisa);

    }
    public Request(){}

    public String getTypeOfService() {
        return typeOfService;
    }

    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle job) {
        this.vehicle = job;
    }

    public String getProposedDate() {
        return proposedDate;
    }

    public void setProposedDate(String proposedDate) {
        this.proposedDate = proposedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProposedTime() {
        return proposedTime;
    }

    public void setProposedTime(String proposedTime) {
        this.proposedTime = proposedTime;
    }
}
