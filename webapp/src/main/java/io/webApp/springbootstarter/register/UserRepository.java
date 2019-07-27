package io.webApp.springbootstarter.register;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface class for user register table, inherits JpaRepository.
 * 
 * @author satishkumaranbalagan
 *
 */
public interface UserRepository extends JpaRepository<register, Integer> {

}
