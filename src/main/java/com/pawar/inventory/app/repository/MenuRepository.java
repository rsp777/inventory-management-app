package com.pawar.inventory.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.app.model.Menu;

@Repository
public interface MenuRepository  extends JpaRepository<Menu, Integer>{

}

