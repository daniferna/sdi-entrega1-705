package com.uniovi.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Offer {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private Date date;
    private String description;
    private Double value;
    private Boolean buy = false;

    @OneToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Offer() {
    }

    public Offer(Long id, String description, Double value) {
        super();
        this.id = id;
        this.description = description;
        this.value = value;
    }

    public Offer(String title, String description, Double value, User user) {
        super();
        setTitle(title);
        setDate(new Date());
        this.description = description;
        this.value = value;
        this.user = user;
    }

    public Offer(String description, Double value, User user) {
        super();
        this.description = description;
        this.value = value;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double score) {
        this.value = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getBuy() {
        return buy;
    }

    public void setBuy(Boolean buy) {
        this.buy = buy;
    }

    @Override
    public String toString() {
        return "Offer [id=" + id + ", description=" + description + ", value=" + value + "]";
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
