package com.yeshwanth.sbr.repository;

import com.yeshwanth.sbr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

}
