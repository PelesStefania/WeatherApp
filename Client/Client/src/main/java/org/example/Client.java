package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String HOST = "localhost";
    private final int PORT = 6543;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Role currentRole; // Rolul utilizatorului curent (USER sau ADMIN)
    private String currentUsername; // store after login or sign up

    public void start() {
        try {
            Socket socket = new Socket(HOST, PORT);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            // Thread pentru log in sau sign up
            new Thread(() -> {
                boolean authenticated = false;
                Scanner scanner = new Scanner(System.in);

                while (!authenticated) {
                    System.out.println("""
                            Choose an action:
                            1) Sign up
                            2) Log in
                            3) Exit
                            """);

                    String choice = scanner.nextLine();
                    switch (choice) {
                        case "1" -> authenticated = handleSignUp(scanner);
                        case "2" -> authenticated = handleLogIn(scanner);
                        case "3", "exit" -> {
                            System.out.println("Exiting client...");
                            System.exit(0); // Termină aplicația
                        }
                        default -> System.out.println("Invalid choice. Please try again.");
                    }
                }

                // După autentificare, pornim thread-urile principale pentru scriere și citire
                sendRequest();
            }).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequest() {
        // Thread pentru trimiterea mesajelor către server
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (currentRole == Role.ADMIN) {
                    System.out.println("""
                        Choose an action:
                        1) Get weather
                        2) Import weather data (Admin only)
                        3) Exit
                        """);
                } else {
                    System.out.println("""
                            Choose an action:
                            1) Get weather
                            2) Exit
                            """);


                }

                String choice = scanner.nextLine();

                if (currentRole == Role.ADMIN) {
                    // Admin options
                    if ("3".equals(choice) || "exit".equalsIgnoreCase(choice)) {
                        System.out.println("Exiting client...");
                        break;
                    }

                    switch (choice) {
                        case "1" -> showWeatherData();
                        case "2" -> handleWeatherData();
                        default -> System.out.println("Invalid choice. Try again.");
                    }
                } else {

                    switch (choice) {
                        case "1" -> System.out.println("Feature not implemented yet: Set location");
                        case "2" -> System.out.println("Exiting client...");
                        default -> System.out.println("Invalid choice. Try again.");
                    }
                }
            }
        }).start();


        // Thread pentru citirea răspunsurilor de la server
        new Thread(() -> {
            while (true) {
                try {
                    String serverData = (String) this.in.readObject();
                    System.out.println("Server: " + serverData);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    private void handleWeatherData() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter date yyyy-MM-dd:");
        String date = scanner.nextLine();

        System.out.println("Enter weather description:");
        String weatherDescription = scanner.nextLine();

        System.out.println("Enter temperature:");
        Double temperature = Double.parseDouble(scanner.nextLine());

        System.out.println("Enter longitude:");
        Double longitude = Double.parseDouble(scanner.nextLine());

        System.out.println("Enter latitude:");
        Double latitude = Double.parseDouble(scanner.nextLine());

        System.out.println("Enter location name:");
        String locationName = scanner.nextLine();

        // Creează un obiect Request folosind constructorul specificat
        Request request = new Request(date, weatherDescription, temperature, longitude, latitude, locationName,CommandType.ADD_WEATHER);

        try {
            // Trimite cererea la server
            sendRequest(request);
            System.out.println("Data sent to the server.");
        } catch (IOException e) {
            System.err.println("Error sending request: " + e.getMessage());
        }
    }
    private void showWeatherData() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Solicită informațiile de la utilizator
            System.out.println("Enter location name:");
            String locationName = scanner.nextLine();

            System.out.println("Enter latitude:");
            Double latitude = Double.parseDouble(scanner.nextLine());

            System.out.println("Enter longitude:");
            Double longitude = Double.parseDouble(scanner.nextLine());

            // Creează cererea pentru prognoza meteo
            Request request = new Request(locationName,latitude,longitude,CommandType.GET_WEATHER);
            request.setCommandType(CommandType.GET_WEATHER);


            // Trimite cererea la server
            sendRequest(request);

            // Așteaptă răspunsul de la server
            String response = (String) in.readObject();
            System.out.println("Weather forecast for the next 3 days:");
            System.out.println(response);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input for latitude or longitude. Please enter valid numbers.");
        }
    }


    private boolean handleSignUp(Scanner scanner) {
        try {
            System.out.println("Enter your username:");
            String username = scanner.nextLine();
            System.out.println("Enter your email:");
            String email = scanner.nextLine();
            System.out.println("Enter your password:");
            String password = scanner.nextLine();

            Request request = new Request(email, password, username, CommandType.SIGNUP);
            sendRequest(request);

            String response = (String) this.in.readObject();
            System.out.println("Server Response: " + response);
            if ("Sign up successful!".equals(response)) {
                this.currentUsername = username;
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    private boolean handleLogIn(Scanner scanner) {
        try {
            System.out.println("Enter your email:");
            String email = scanner.nextLine();
            System.out.println("Enter your password:");
            String password = scanner.nextLine();

            Request request = new Request(email, password, CommandType.LOGIN);
            sendRequest(request);

            String response = (String) this.in.readObject();
            System.out.println("Server Response: " + response);
            if (response.startsWith("Login successful")) {

                String[] parts = response.split(":");

                if (parts.length < 3) {
                    System.out.println("Invalid server response. Missing role or username.");
                    return false;
                }

                this.currentUsername =  parts[1].trim(); // Extrage username-ul din răspuns
                this.currentRole = Role.valueOf(parts[2].trim()); // Extrage rolul
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    private void sendRequest(Request request) throws IOException {
        String requestJson = new Gson().toJson(request);
        this.out.writeObject(requestJson);
    }


}


