package com.uniovi.repositories;

import com.uniovi.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UsersRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User SET money = ?1 where id = ?2")
    void updateMoney(Float money, long id);
}
