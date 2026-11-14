package pl.mkowalik.guestflow.guest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import pl.mkowalik.guestflow.guest.enums.GuestStatus;
import pl.mkowalik.guestflow.guest.enums.GuestType;
import pl.mkowalik.guestflow.sponsor.Sponsor;

@Entity
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private GuestType type;
    
    
    @Enumerated(EnumType.STRING)
    private GuestStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "sponsor_id")
    private Sponsor sponsor;

    public Guest(){}

    public Guest(String name, String email, GuestType type) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.status = GuestStatus.PENDING_APPROVAL;
    }

    // ---------- Methods ----------

    public void assignSponsor(Sponsor sponsor) {
        if (this.type != GuestType.SPONSORED){
            throw new IllegalArgumentException("Sponsor must be set only for SPONSORED guests");
        }
        setSponsor(sponsor);
    }

    // ---------- Getters and Setters ----------

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public GuestStatus getStatus() {
        return status;
    }

    public GuestType getType() {
        return type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    private  void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    public  void setStatus(GuestStatus status) {
        this.status = status;
    }

    public void setType(GuestType type) {
        this.type = type;
    }
}
