package com.imotspot.model.imot;

import com.imotspot.enumerations.Condition;
import com.imotspot.enumerations.ImotType;
import com.imotspot.interfaces.Media;
import com.imotspot.model.User;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Imot implements Serializable {

    private User owner;
    private float price;
    private String year;
    private String description;
    private Date published;
    private Location location;
    private ImotType type;
    private Condition condition;
    private Picture frontImage;
    private List<Media> media = new ArrayList<>();
    private List<Feature> features = new ArrayList<>();
    private List<Appliance> appliances = new ArrayList<>();

    public Imot(Location location) {
        this.location = location;
    }
}
