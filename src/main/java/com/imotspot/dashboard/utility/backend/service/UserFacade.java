package com.imotspot.dashboard.utility.backend.service;

import com.imotspot.dashboard.utility.backend.User;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Matti Tahvonen
 */
@Stateless
public class UserFacade {
    
    @Inject
    UserRepository repo;

    public User findByEmail(String email) {
        try {
            return repo.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    public User save(User user) {
        return repo.save(user);
    }

}
