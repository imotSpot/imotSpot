package com.imotspot.dashboard.utility.backend.service;


import com.imotspot.dashboard.utility.backend.User;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository(forEntity = User.class)
public interface UserRepository extends EntityRepository<User, Long> {

    public User findByEmail(String email);
   
}