package hitec.com.model;

public class UserItem {
    private String username;
    private boolean selected;

    public UserItem() {
        username = "";
        selected = false;
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
}
