package com.uniovi.services;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void init() {
    }

    public List<User> getUsers(User user) {
        List<User> users = new ArrayList<User>();
        if (user == null)
            return users;
        if (user.getRole().equals(rolesService.getRoles()[0])) //Usuario normal
            users.add(usersRepository.findByEmail(user.getEmail()));
        else
            usersRepository.findAll().forEach(users::add);
        return users;
    }

    public User getUser(Long id) {
        return usersRepository.findById(id).get();
    }

    public void addUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public float getActualUserMoney() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = getUserByEmail(email);
            return user.getMoney();
        }
        return 0;
    }

    public void updateMoney(User user, Float money) {
        usersRepository.updateMoney(money, user.getId());
    }

    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }

    public boolean canModifyOffer(User user, Offer offer) {
        if (user.getOffers().contains(offer)) return true;
        return !user.getRole().equals(rolesService.getRoles()[0]);
    }
}
