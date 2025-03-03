package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final UserRepository userRepository;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.userRepository = UserRepository.getInstance();
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            while (true) {
                String requestJson = (String) in.readObject();
                Request request = new Gson().fromJson(requestJson, Request.class);

                String response = processRequest(request);
                out.writeObject(response);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private String processRequest(Request request) {
        if (request.getCommandType() == CommandType.SIGNUP) {
            return userRepository.signUp(request.getUsername(), request.getEmail(), request.getPassword());
        } else if (request.getCommandType() == CommandType.LOGIN) {
            return userRepository.logIn(request.getEmail(), request.getPassword());
        }else if(request.getCommandType() == CommandType.ADD_WEATHER) {
            return userRepository.saveWeather(request.getDate(), request.getWeatherDescription(), request.getTemperature(), request.getLongitude(), request.getLatitude(), request.getLocationName());
        }else if(request.getCommandType() == CommandType.GET_WEATHER) {
            return userRepository.getWeatherForecast(request.getLatitude(), request.getLongitude(), request.getLocationName());
        }else {
            return "Unknown command.";
        }
    }
}