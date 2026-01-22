package com.example.BarterExchange.repository;

import com.example.BarterExchange.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
