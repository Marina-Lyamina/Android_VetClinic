package ru.marinalyamina.vetclinic.models;

public class Procedure {
    private Long id;
    private String name;
    private Integer price;

    public Procedure(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
    //    private List<Appointment> appointments;
}

