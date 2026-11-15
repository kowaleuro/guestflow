package pl.mkowalik.guestflow.sponsor;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import pl.mkowalik.guestflow.auth.account.Account;
import pl.mkowalik.guestflow.guest.Guest;

@Entity
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "sponsor")
    private List<Guest> guests = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne(optional = true)
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    public Sponsor(){}

    public Sponsor(Account account, String name, String email) {
        this.account = account;
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
