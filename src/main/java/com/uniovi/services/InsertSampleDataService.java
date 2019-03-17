package com.uniovi.services;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

//Eliminado @Service para desactivarlo
@Service
public class InsertSampleDataService {
    @Autowired
    private UsersService usersService;

    @Autowired
    private RolesService rolesService;

    @PostConstruct
    public void init() {
        User user1 = new User("user1@email.com", "Pedro", "Díaz");
        user1.setPassword("user1");
        user1.setRole(rolesService.getRoles()[0]);
        User user2 = new User("user2@email.com", "Lucas", "Núñez");
        user2.setPassword("123456");
        user2.setRole(rolesService.getRoles()[0]);
        User user3 = new User("user3@email.com", "María", "Rodríguez");
        user3.setPassword("123456");
        user3.setRole(rolesService.getRoles()[0]);
        User user4 = new User("user4@email.com", "Marta", "Almonte");
        user4.setPassword("123456");
        user4.setRole(rolesService.getRoles()[0]);
        User user5 = new User("admin@email.com", "Daniel", "Fernandez");
        user5.setPassword("admin");
        user5.setRole(rolesService.getRoles()[1]);
        Set user1Offers = new HashSet<Offer>() {
            {
                add(new Offer("Chupete", "Chupete gastado", 10.0, user1));
                add(new Offer("TV", "Tele vieja", 9.0, user1));
                add(new Offer("Consola vieja", "PS3", 7.0, user1));
                add(new Offer("Consola nueva", "PS4", 9.5, user1));
            }
        };
        user1.setOffers(user1Offers);
        Set user2Offers = new HashSet<Offer>() {
            {
                add(new Offer("Ob1", "Objeto B1", 5.0, user2));
                add(new Offer("Ob2", "Objeto B2", 4.3, user2));
                add(new Offer("Ob3", "Objeto B3", 8.0, user2));
                add(new Offer("Ob4", "Objeto B4", 3.5, user2));
                add(new Offer("Objeto caro", "Muy caro", 150d, user2));
            }
        };
        user2.setOffers(user2Offers);
        Set user3Offers = new HashSet<Offer>() {
            {
                add(new Offer("Oc1", "Objeto C1", 5.5, user3));
                add(new Offer("Oc2", "Objeto C2", 6.6, user3));
                add(new Offer("Oc3", "Objeto C3", 7.0, user3));
            }
        };
        user3.setOffers(user3Offers);
        Set user4Offers = new HashSet<Offer>() {
            {
                add(new Offer("Od1", "Objeto D1", 10.0, user4));
                add(new Offer("Od2", "Objeto D2", 8.0, user4));
                add(new Offer("Od3", "Objeto D3", 9.0, user4));
            }
        };
        user4.setOffers(user4Offers);
        usersService.addUser(user1);
        usersService.addUser(user2);
        usersService.addUser(user3);
        usersService.addUser(user4);
        usersService.addUser(user5);
    }
}