package com.pawar.inventory.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.app.model.MenuAccess;
import com.pawar.inventory.app.model.MenuAccessId;

@Repository
public interface MenuAccessRepository extends JpaRepository<MenuAccess, MenuAccessId> {

	List<MenuAccess> findMenuAccessesByMenuId(int menu_id);


}
