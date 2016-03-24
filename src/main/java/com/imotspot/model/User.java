package com.imotspot.model;

import com.imotspot.model.imot.Imot;

import java.io.Serializable;
import java.util.List;

public interface User extends Serializable {
    List<Imot> imots();

    void addImot(Imot imo);

    void removeImot(Imot imo);

    void clearImots();

    String oauthIdentifier();

    String role();

    String firstName();

    String lastName();

    String title();

    boolean male();

    String email();

    String location();

    String phone();

    Integer newsletterSubscription();

    String website();

    String bio();

    String picUrl();

}
