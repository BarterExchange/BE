package com.example.BarterExchange.repository;

import com.example.BarterExchange.domain.Offer;
import com.example.BarterExchange.domain.OfferStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    boolean existsByItemIdAndStatus(Long itemId, OfferStatus status);

    @Query("select o from Offer o join fetch o.item join fetch o.proposer where o.id = :offerId")
    Optional<Offer> findByIdWithItemAndProposer(@Param("offerId") Long offerId);

    @Modifying
    @Query("update Offer o set o.status = :status where o.item.id = :itemId and o.id <> :offerId and o.status = com.example.BarterExchange.domain.OfferStatus.PENDING")
    int updateOtherOffersStatus(@Param("itemId") Long itemId, @Param("offerId") Long offerId, @Param("status") OfferStatus status);
}
