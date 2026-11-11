package pl.mkowalik.guestflow.guest;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
interface GuestRepository extends JpaRepository<Guest, Long>{
    Optional<Guest> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Guest> findByStatus(GuestStatus status);
    List<Guest> findBySponsorId(Long sponsorId);
}