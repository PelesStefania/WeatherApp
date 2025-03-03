package org.example;

import jakarta.persistence.*;

import java.time.LocalDate;
//ADMIN:
//introduce tot la weather si location
//creez obiect tip location, si un constructor

@Entity
@Table(name = "weather")
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // AUTO pentru H2
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "weather_description", nullable = false, length = 255)
    private String weatherDescription;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public Weather(Location location, Double temperature, String weatherDescription, LocalDate date) {
        this.location = location;
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
        this.date = date;
        //voi avea nevoie de un setter
    }

    public Weather() {

    }

    public Long getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public LocalDate getDate() {
        return date;
    }
}
