package com.uniovi.repositories;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface OffersRepository extends CrudRepository<Offer, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Offer SET buy = ?1 WHERE id = ?2")
    void updateBuy(Boolean buy, Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Offer SET buyer = ?1 WHERE id = ?2")
    void updateBuyer(User buyer, Long id);

    @Query("SELECT r FROM Offer r WHERE r.user = ?1 ORDER BY r.id ASC ")
    Page<Offer> findAllByUser(Pageable pageable, User user);

    @Query("SELECT r FROM Offer r WHERE r.buyer = ?1 ORDER BY r.id ASC ")
    Page<Offer> findAllBoughtByUser(Pageable pageable, User user);

    @Query("SELECT r FROM Offer r WHERE r.user = ?1 ORDER BY r.id ASC ")
    List<Offer> findAllByUser(User user);

    @Query("SELECT r FROM Offer r WHERE r.user <> ?1 ORDER BY r.id ASC ")
    Page<Offer> findAllAvailableForUser(Pageable pageable, User user);

    @Query("SELECT r FROM Offer r WHERE (LOWER(r.title) LIKE LOWER(?1))")
    Page<Offer> searchByTitle(Pageable pageable, String seachtext);

    @Query("SELECT r FROM Offer r WHERE (LOWER(r.description) LIKE LOWER(?1) OR LOWER(r.user.name) LIKE LOWER(?1)) AND r.user = ?2 ")
    Page<Offer> searchByDescriptionNameAndUser(Pageable pageable, String seachtext, User user);

    @Query("SELECT r FROM Offer r WHERE (LOWER(r.title) LIKE LOWER(?1)) AND r.user <> ?2 ")
    Page<Offer> searchAvaiableByTitle(Pageable pageable, String seachtext, User user);

    Page<Offer> findAll(Pageable pageable);

    @Query("SELECT r FROM Offer r WHERE (LOWER(r.title) LIKE LOWER(?1)) AND r.buyer = ?2 ")
    Page<Offer> searchBoughtByTitle(Pageable pageable, String searchText, User user);
}
