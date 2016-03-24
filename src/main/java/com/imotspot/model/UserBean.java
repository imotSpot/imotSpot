package com.imotspot.model;

import com.imotspot.model.imot.Imot;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
public final class UserBean implements User {
    private static final long serialVersionUID = 1L;

    @NonNull
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

    @Singular
    @Getter(AccessLevel.NONE)
    private List<Imot> imots = new ArrayList<>();

    @Override
    public List<Imot> imots() {
        List<Imot> imotsCopy = new ArrayList<>();
        for (Imot imo : imots) {
            imotsCopy.add(imo);
        }
        return imotsCopy;
    }

    @Override
    public void addImot(Imot imo) {
//        imo.setOwner(this);
        imots.add(imo);
    }

    @Override
    public void removeImot(Imot imo) {
        imots.remove(imo);
    }

    @Override
    public void clearImots() {
        imots.clear();
    }
}
