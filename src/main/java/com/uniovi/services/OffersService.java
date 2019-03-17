package com.uniovi.services;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.repositories.OffersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OffersService {

    @Autowired
    private OffersRepository offersRepository;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private UsersService usersService;

    public Page<Offer> getOffers(Pageable pageable) {
        Page<Offer> offers = offersRepository.findAll(pageable);
        return offers;
    }

    public Offer getOffer(Long id) {
        Set<Offer> consultedList = (Set<Offer>) httpSession.getAttribute("consultedList");
        if (consultedList == null)
            consultedList = new HashSet<Offer>();
        Offer offerObtained = offersRepository.findById(id).get();
        consultedList.add(offerObtained);
        httpSession.setAttribute("consultedList", consultedList);
        return offerObtained;
    }

    public List<Offer> getOffersForUser(User user) {
        return offersRepository.findAllByUser(user);
    }

    public Page<Offer> getBoughtOffers(Pageable pageable, User user) {
        return offersRepository.findAllBoughtByUser(pageable, user);
    }

    public Page<Offer> getOffersAvailableForUser(Pageable pageable, User user) {
        Page<Offer> offers;
        if (user.getRole().equals(rolesService.getRoles()[0])) {
            offers = offersRepository.findAllAvailableForUser(pageable, user);
        } else {
            offers = getOffers(pageable);
        }
        return offers;
    }

    public Page<Offer> searchOffersAvaiableByTitle(Pageable pageable, String searchText, User user) {
        Page<Offer> offers = new PageImpl<Offer>(new ArrayList<Offer>());
        searchText = "%" + searchText + "%";
        if (user.getRole().equals(rolesService.getRoles()[0])) {
            offers = offersRepository.searchAvaiableByTitle(pageable, searchText, user);
        } else {
            offers = offersRepository.searchByTitle(pageable, searchText);
        }
        return offers;
    }

    public Page<Offer> searchOffersBoughtByTitleForUser(Pageable pageable, String searchText, User user) {
        Page<Offer> offers;
        searchText = "%" + searchText + "%";
        offers = offersRepository.searchBoughtByTitle(pageable, searchText, user);
        return offers;
    }

    public void setOfferBuy(boolean buy, Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User buyer = usersService.getUserByEmail(email);
        Offer offer = offersRepository.findById(id).get();
        if (offer.getUser().getEmail().equals(email))
            return;
        offersRepository.updateBuy(buy, id);
        if (buy)
            offersRepository.updateBuyer(buyer, id);
        else
            offersRepository.updateBuyer(null, id);
    }

    public void addOffer(Offer offer) {
        offersRepository.save(offer);
    }

    public void deleteOffer(Long id) {
        offersRepository.deleteById(id);
    }

}
