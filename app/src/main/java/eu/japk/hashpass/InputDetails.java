package eu.japk.hashpass;

public class InputDetails {

    private String username;
    private String password;

    public InputDetails(String user, String pass){
        username = user;
        password = pass;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}
