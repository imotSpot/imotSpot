package com.imotspot.dashboard.domain.imot;

import com.imotspot.dashboard.domain.User;
import lombok.Data;

import java.io.Serializable;
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
    private Condition condition;
    private List<Media> media;
    private List<Feature> fixtures;
    private List<Appliance> appliances;

}
