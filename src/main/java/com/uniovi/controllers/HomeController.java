package com.uniovi.controllers;

import com.uniovi.entities.User;
import com.uniovi.services.OffersService;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    UsersService usersService;

    @Autowired
    OffersService offersService;

    @RequestMapping("/")
    public String index() {
        return "home";
    }

    @RequestMapping(value = {"/home"}, method = RequestMethod.GET)
    public String home(Model model, HttpSession httpSession, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User activeUser = usersService.getUserByEmail(email);
        model.addAttribute("offerList", offersService.getOffersForUser(activeUser));
        httpSession.setAttribute("money", usersService.getActualUserMoney());
        return "home";
    }

}
