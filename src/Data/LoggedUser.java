package Data;

public class LoggedUser {

    private int user_id;
    private String username;
    private String password;

    public LoggedUser(){

    }
    public LoggedUser(int userId, String username, String password){
        user_id = userId;
        this.username = username;

        this.password = password;

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}