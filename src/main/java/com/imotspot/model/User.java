package com.imotspot.model;

import com.imotspot.model.imot.Imot;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public final class User {
    private String oauthIdentifier;
    private String role;
    private String firstName;
    private String lastName;
    private String title;
    private boolean male;
    private String email;
    private String location;
    private String phone;
    private Integer newsletterSubscription;
    private String website;
    private String bio;
    private String picUrl;
    private List<Imot> imots = new ArrayList<>();

    public User(String oauthIdentifier) {
        this.oauthIdentifier = oauthIdentifier;
    }

}
