package org.example;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;

public class UserRepository {
    private static UserRepository instance;
    private final EntityManagerFactory emf;

    private UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("weather_app");
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public synchronized String signUp(String username, String email, String password) {
        System.out.println("Provided password: " + password);
        System.out.println("Provided email: " + email);
        System.out.println("Provided name: " + username);
        if (!isValidEmail(email)) {
            return "Invalid email format.";
        }

        EntityManager em = emf.createEntityManager();
        try {
            // Verifică dacă utilizatorul există deja
            List<AppUser> existingUsers = em.createQuery("SELECT u FROM AppUser u WHERE u.email = :email", AppUser.class)
                    .setParameter("email", email)
                    .getResultList();
            if (!existingUsers.isEmpty()) {
                return "User already exists.";
            }

            // Creează un utilizator nou
            AppUser user = new AppUser(username, password, email,Role.USER);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            return "Sign up successful!";
        } finally {
            em.close();
        }
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    public synchronized String logIn(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            List<AppUser> users = em.createQuery("SELECT u FROM AppUser u WHERE u.email = :email", AppUser.class)
                    .setParameter("email", email)
                    .getResultList();
            if (users.isEmpty()) {
                return "User does not exist.";
            }


            AppUser user = users.get(0);

            if (!user.getPassword().equals(password)) {
                return "Invalid password.";
            }

            return "Login successful: " + user.getUsername()+ ":" + user.getRole();
        } finally {
            em.close();
        }
    }
    public synchronized String saveWeather(String date,String weatherDescription, Double temperature, Double longitude, Double latitude, String locationName) {


        System.out.println("Saving weather data:");
        System.out.println("Date: " + date);
        System.out.println("Weather Description: " + weatherDescription);
        System.out.println("Temperature: " + temperature);
        System.out.println("Latitude: " + latitude);
        System.out.println("Longitude: " + longitude);
        System.out.println("Location Name: " + locationName);
        EntityManager em = emf.createEntityManager();

        try {

                em.getTransaction().begin();


            // Verifică dacă locația există deja în baza de date folosind Optional
            Optional<Location> optionalLocation = em.createQuery(
                            "SELECT l FROM Location l WHERE l.name = :name AND l.latitude = :latitude AND l.longitude = :longitude", Location.class)
                    .setParameter("name", locationName)
                    .setParameter("latitude", latitude)
                    .setParameter("longitude", longitude)
                    .getResultStream() // Obține un stream din rezultatele query-ului
                    .findFirst(); // Returnează un Optional<Location>

            Location location = optionalLocation.orElseGet(() -> {
                // Creează o locație nouă dacă nu există
                Location newLocation = new Location(locationName, latitude, longitude);
                em.persist(newLocation); // Persistăm locația nouă
                return newLocation;
            });



            // Creează o nouă intrare pentru vreme
            Weather weather = new Weather(location, temperature, weatherDescription, LocalDate.parse(date));
            location.getWeatherList().add(weather);
            em.persist(weather);

            em.getTransaction().commit();
            return "Weather data saved successfully!";
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return "Error saving weather data: " + e.getMessage();
        } finally {
            em.close();
        }
    }

    public synchronized String getWeatherForecast(Double latitude, Double longitude, String locationName) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Verifică dacă locația există
            List<Location> locations = em.createQuery(
                            "SELECT l FROM Location l WHERE l.name = :name AND l.latitude = :latitude AND l.longitude = :longitude", Location.class)
                    .setParameter("name", locationName)
                    .setParameter("latitude", latitude)
                    .setParameter("longitude", longitude)
                    .getResultList();

            if (locations.isEmpty()) {
                return "No data available for the given location.";
            }

            Location location = locations.get(0);

            // Obține data curentă
            LocalDate currentDate = LocalDate.now();

            // Selectează prognoza pe 3 zile
            List<Weather> forecast = em.createQuery(//daca da eroare sa caut doar dupa nume
                            "SELECT w FROM Weather w WHERE w.location = :location AND w.date >= :startDate AND w.date <= :endDate", Weather.class)
                    .setParameter("location", location)
                    .setParameter("startDate", currentDate)
                    .setParameter("endDate", currentDate.plusDays(3))
                    .getResultList();

            if (forecast.isEmpty()) {
                return "No forecast available for the next 3 days.";
            }

            // Construiește răspunsul
            StringBuilder response = new StringBuilder();
            response.append("Weather forecast for ").append(locationName).append(":\n");
            for (Weather weather : forecast) {
                response.append("Date: ").append(weather.getDate())
                        .append(", Temperature: ").append(weather.getTemperature())
                        .append(", Description: ").append(weather.getWeatherDescription())
                        .append("\n");
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching weather forecast: " + e.getMessage();
        } finally {
            em.close();
        }
    }



    //un meniu in care cer date de la utilizator:
    //admin- apasa pe save weather, sa introduca, numele locatiei, latitudine longitudine,
}