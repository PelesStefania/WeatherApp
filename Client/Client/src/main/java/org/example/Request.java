package org.example;

public class Request {
    private String email;
    private String password;
    private String username; // Folosit doar pentru sign up
    private CommandType commandType;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private String weatherDescription;
    private String date;

    public Request() {
        // Constructor implicit
    }

    public Request(String date,String weatherDescription, Double temperature, Double longitude, Double latitude, String locationName, CommandType commandType) {
        this.date = date;
        this.weatherDescription = weatherDescription;
        this.temperature = temperature;
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationName = locationName;
        this.commandType = commandType;

    }

    public Request(String email, String password, CommandType commandType) {
        this.email = email;
        this.password = password;
        this.commandType = commandType;
    }

    public Request(String email, String password, String username, CommandType commandType) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.commandType = commandType;
    }

    public Request(String locationName, Double latitude, Double longitude,CommandType commandType) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.commandType = commandType;
    }

    // Getters și Setters
    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
}