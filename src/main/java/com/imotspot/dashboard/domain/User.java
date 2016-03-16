package com.imotspot.dashboard.domain;

import lombok.Data;

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
}
