package com.example.LibraryMS.UserRepo;

import com.example.LibraryMS.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User,Integer> {
    UserDetails findByEmail(String username);
}
