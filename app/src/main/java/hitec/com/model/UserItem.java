package hitec.com.model;

public class UserItem {
    private String username;
    private boolean selected;
    private String createdAt;

    public UserItem() {
        username = "";
        selected = false;
        createdAt = "";
    }

    public void setUserName(String value) {
        username = value;
    }

    public String getUsername() {
        return username;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setCreatedAt(String value) {
        this.createdAt = value;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
