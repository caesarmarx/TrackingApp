package hitec.com.model;

public class LocationItem {
    private String address;
    private String time;
    private String latitude;
    private String longitude;

    public LocationItem() {
        address = "";
        time = "";
        latitude = "";
        longitude = "";
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
}
