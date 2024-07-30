package com.pawar.inventory.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.app.model.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

	Menu findMenuByMenuName(String menuName);

}
