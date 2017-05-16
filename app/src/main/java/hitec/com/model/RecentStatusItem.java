package hitec.com.model;

public class RecentStatusItem {
    private String username;
    private String message;
    private String imageURL;
    private String time;
    private int status;

    public RecentStatusItem() {
        username = "";
        message = "";
        imageURL = "";
        time = "";
        status = 0;
    }

    public void setUserName(String value) {
        this.username = value;
    }

    public String getUsername() {
        return username;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getMessage() {
        return message;
    }

    public void setImageURL(String value) {
        this.imageURL = value;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setTime(String value) {
        this.time = value;
    }

    public String getTIme() {
        return time;
    }

    public void setStatus(int value) {
        this.status = value;
    }

    public int getStatus() {
        return status;
    }
}
