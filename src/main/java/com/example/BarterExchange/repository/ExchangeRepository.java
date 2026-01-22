package com.example.BarterExchange.repository;

import com.example.BarterExchange.domain.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
