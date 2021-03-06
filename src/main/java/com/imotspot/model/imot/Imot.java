package com.imotspot.model.imot;

import com.imotspot.model.imot.enumerations.Condition;
import com.imotspot.model.imot.enumerations.ImotType;
import com.imotspot.model.User;
import com.imotspot.model.imot.interfaces.Media;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
//@Accessors(fluent = true)
public class Imot implements Serializable {
    private static final long serialVersionUID = 1L;

    private User owner;
    private float price;
    private ImotType type;
    private String year;
    private String description;
    private Date published;
    private Location location;
    private Condition condition;
    private Picture frontImage;
    private List<Media> media = new ArrayList<>();
    private List<Feature> features = new ArrayList<>();
    private List<Appliance> appliances = new ArrayList<>();

    public Imot(Location location) {
        this.location = location;
    }
}
