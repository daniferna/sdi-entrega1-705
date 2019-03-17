package com.uniovi.controllers;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.services.OffersService;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class OffersController {

    @Autowired
    private OffersService offersService;

    @Autowired
    private UsersService usersService;

    @RequestMapping("/offer/list")
    public String getList(Model model, Pageable pageable, Principal principal, HttpSession httpSession,
                          @RequestParam(value = "", required = false) String searchText) {
        String email = principal.getName(); //El email es el nombre con el que se autentica
        User user = usersService.getUserByEmail(email);
        Page<Offer> offers;
        if (searchText != null && !searchText.isEmpty()) {
            offers = offersService.searchOffersAvaiableByTitle(pageable, searchText, user);
        } else {
            offers = offersService.getOffersAvailableForUser(pageable, user);
        }
        model.addAttribute("offerList", offers.getContent());
        model.addAttribute("page", offers);
        model.addAttribute("noMoney", null);
        httpSession.setAttribute("money", usersService.getActualUserMoney());
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha accedido a la lista de ofertas");
        return "offer/list";
    }

    @RequestMapping(value = "/offer/add", method = RequestMethod.POST)
    public String setOffer(@ModelAttribute Offer offer, Principal principal) {
        User user = usersService.getUserByEmail(principal.getName());
        offer.setUser(user);
        offer.setDate(new Date());
        offersService.addOffer(offer);
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha añadido la oferta con id" + offer.getId());
        return "redirect:/home";
    }

    @RequestMapping("/offer/list/update")
    public String updateList(Model model, HttpSession httpSession, Pageable pageable, Principal principal) {
        String email = principal.getName();
        User user = usersService.getUserByEmail(email);
        Page<Offer> offers = offersService.getOffersAvailableForUser(pageable, user);
        model.addAttribute("offerList", offers.getContent());
        model.addAttribute("moneyUser", usersService.getActualUserMoney());
        httpSession.setAttribute("money", usersService.getActualUserMoney());
        return "offer/list :: tableOffers";
    }

    @RequestMapping("/offer/boughtList")
    public String boughtList(Model model, Pageable pageable, Principal principal,
                             @RequestParam(value = "", required = false) String searchText) {
        String email = principal.getName(); //El email es el nombre con el que se autentica
        User user = usersService.getUserByEmail(email);
        Page<Offer> offers;
        if (searchText != null && !searchText.isEmpty()) {
            offers = offersService.searchOffersBoughtByTitleForUser(pageable, searchText, user);
        } else {
            offers = offersService.getBoughtOffers(pageable, user);
        }
        model.addAttribute("boughtList", offers.getContent());
        model.addAttribute("page", offers);
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha accedido a la lista de ofertas compradas");
        return "offer/boughtList";
    }

    @RequestMapping("/offer/list/updateBought")
    public String updateBoughtList(Model model, HttpSession httpSession, Pageable pageable, Principal principal) {
        String email = principal.getName();
        User user = usersService.getUserByEmail(email);
        Page<Offer> offers = offersService.getBoughtOffers(pageable, user);
        model.addAttribute("boughtList", offers.getContent());
        httpSession.setAttribute("money", usersService.getActualUserMoney());
        return "offer/boughtList :: tableOffers";
    }

    @RequestMapping(value = "/offer/add")
    public String getOffer(Model model, Principal principal) {
        User user = usersService.getUserByEmail(principal.getName());
        model.addAttribute("usersList", usersService.getUsers(user));
        return "offer/add";
    }

    @RequestMapping(value = "/offer/edit/{id}", method = RequestMethod.POST)
    public String setEdit(Model model, Principal principal, @PathVariable Long id, @ModelAttribute Offer offer) {
        User user = usersService.getUserByEmail(principal.getName());
        Offer original = offersService.getOffer(id);
        if (!usersService.canModifyOffer(user, original))
            return "redirect:/home";
        // modificar solo score y description
        original.setValue(offer.getValue());
        original.setDescription(offer.getDescription());
        original.setTitle(offer.getTitle());
        offersService.addOffer(original);
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha editado la oferta con id" + offer.getId());
        return "redirect:/offer/details/" + id;
    }

    @RequestMapping(value = "/offer/edit/{id}")
    public String getEdit(Model model, Principal principal, @PathVariable Long id) {
        User user = usersService.getUserByEmail(principal.getName());
        if (!usersService.canModifyOffer(user, offersService.getOffer(id)))
            return "redirect:/home";
        model.addAttribute("offer", offersService.getOffer(id));
        model.addAttribute("usersList", usersService.getUsers(user));
        return "offer/edit";
    }

    @RequestMapping("/offer/details/{id}")
    public String getDetail(Model model, Principal principal, @PathVariable Long id) {
        model.addAttribute("offer", offersService.getOffer(id));
        User user = usersService.getUserByEmail(principal.getName());
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha accedido al detalle de la oferta con id: " + id);
        return "offer/details";
    }

    @RequestMapping("/offer/delete/{id}")
    public String deleteOffer(@PathVariable Long id, Principal principal) {
        User user = usersService.getUserByEmail(principal.getName());
        offersService.deleteOffer(id);
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha eliminado la oferta con id: " + id);
        return "redirect:/offer/list";
    }

    @RequestMapping(value = "/offer/{id}/buy", method = RequestMethod.GET)
    public String setBuyTrue(Model model, HttpSession httpSession, Principal principal, @PathVariable Long id) {
        User user = usersService.getUserByEmail(principal.getName());
        Offer offer = offersService.getOffer(id);
        if (user.getMoney() < offer.getValue()) {
            httpSession.setAttribute("noMoney",
                    "No tienes suficiente dinero para compar: " + offer.getTitle());
            return "redirect:/offer/list";
        }
        usersService.updateMoney(user, (float) (user.getMoney() - offer.getValue()));
        offersService.setOfferBuy(true, id);
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha comprado la oferta con id: " + id);
        httpSession.setAttribute("noMoney", null);
        httpSession.setAttribute("money", usersService.getActualUserMoney());
        return "redirect:/offer/list";
    }

    @RequestMapping(value = "/offer/{id}/nobuy", method = RequestMethod.GET)
    public String setBuyFalse(Model model, HttpSession httpSession, Principal principal, @PathVariable Long id) {
        User user = usersService.getUserByEmail(principal.getName());
        Offer offer = offersService.getOffer(id);
        usersService.updateMoney(user, (float) (user.getMoney() + offer.getValue()));
        offersService.setOfferBuy(false, id);
        httpSession.setAttribute("money", usersService.getActualUserMoney());
        Logger.getGlobal().log(Level.INFO, user.getId() + " ha devuelto la oferta con id: " + id);
        return "redirect:/offer/boughtList";
    }

}
