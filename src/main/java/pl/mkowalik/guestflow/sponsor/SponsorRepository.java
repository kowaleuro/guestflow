package pl.mkowalik.guestflow.sponsor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorRepository extends JpaRepository<Sponsor, Long>{
    Optional<Sponsor> findByEmail(String email);
    boolean existsByEmail(String email);
}
