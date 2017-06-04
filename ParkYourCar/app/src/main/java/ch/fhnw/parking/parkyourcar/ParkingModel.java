package ch.fhnw.parking.parkyourcar;

/**
 * Created by Melanie on 04.06.2017.
 */

public class ParkingModel {

    private String name;
    private String status;
    private String place;

    private String dbId;

    public ParkingModel(String name, String status, String place, String dbId){
        this.name = name;
        this.status = status;
        this.place = place;
        this.dbId = dbId;
    }


    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getPlace() {
        return place;
    }

    public String getDbId() {
        return dbId;
    }


}
