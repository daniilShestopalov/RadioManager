package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.User;
import cs.vsu.radiomanager.model.enumerate.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRep extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);
    List<User> findByRole(Role role);

}
