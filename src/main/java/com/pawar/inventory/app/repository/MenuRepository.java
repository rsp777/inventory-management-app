package com.pawar.inventory.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.app.model.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

	@Query("select menu from Menu menu order by menu.menu_id asc")
	List<Menu> findAllOrderedByMenuId();

	Menu findMenuByMenuName(String menuName);

}
