package hitec.com.model;

public class LocationItem {
    private String id;
    private String address;
    private String time;
    private String latitude;
    private String longitude;
    private int sent;

    public LocationItem() {
        id = "";
        address = "";
        time = "";
        latitude = "";
        longitude = "";
        sent = 0;
    }

    public void setID(String value) {
        this.id = value;
    }

    public String getID() {
        return id;
    }

    public void setAddress(String value) {
        this.address = value;
    }

    public String getAddress() {
        return this.address;
    }

    public void setTime(String value) {
        this.time = value;
    }

    public String getTime() {
        return this.time;
    }

    public void setLatitude(String value) {
        this.latitude = value;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLongitude(String value) {
        this.longitude = value;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setSend(int value) {
        this.sent = value;
    }

    public int getSend() {
        return sent;
    }
}
