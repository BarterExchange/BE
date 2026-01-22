package com.example.BarterExchange.service;

import com.example.BarterExchange.domain.Exchange;
import com.example.BarterExchange.domain.Item;
import com.example.BarterExchange.domain.ItemStatus;
import com.example.BarterExchange.domain.Offer;
import com.example.BarterExchange.domain.OfferStatus;
import com.example.BarterExchange.domain.User;
import com.example.BarterExchange.repository.ExchangeRepository;
import com.example.BarterExchange.repository.ItemRepository;
import com.example.BarterExchange.repository.OfferRepository;
import com.example.BarterExchange.repository.UserRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OfferService {

    private static final Duration OFFER_LOCK_TTL = Duration.ofSeconds(5);

    private final OfferRepository offerRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;
    private final RedisLockService redisLockService;

    @Transactional
    public Offer createOffer(String proposerUsername, Long itemId) {
        User proposer = userRepository.findByUsername(proposerUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (item.getStatus() != ItemStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item is not available");
        }
        if (item.getOwner().getId().equals(proposer.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot offer on own item");
        }
        Offer offer = new Offer();
        offer.setItem(item);
        offer.setProposer(proposer);
        offer.setStatus(OfferStatus.PENDING);
        return offerRepository.save(offer);
    }

    @Transactional
    public Exchange acceptOffer(String ownerUsername, Long offerId) {
        // Transaction boundary starts here; all state changes are committed atomically.
        Offer offer = offerRepository.findByIdWithItemAndProposer(offerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));
        Item item = offer.getItem();
        User owner = userRepository.findByUsername(ownerUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!item.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can accept offers");
        }

        String lockKey = "lock:item:" + item.getId();
        // Concurrency control: Redis lock coordinates acceptance across multiple instances.
        String lockToken = redisLockService.tryLock(lockKey, OFFER_LOCK_TTL);
        if (lockToken == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another acceptance is in progress");
        }

        try {
            if (item.getStatus() != ItemStatus.ACTIVE) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Item is not available");
            }
            if (offer.getStatus() != OfferStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Offer is not pending");
            }
            if (offerRepository.existsByItemIdAndStatus(item.getId(), OfferStatus.ACCEPTED)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Item already has an accepted offer");
            }

            offer.setStatus(OfferStatus.ACCEPTED);
            item.setStatus(ItemStatus.RESERVED);
            offerRepository.updateOtherOffersStatus(item.getId(), offer.getId(), OfferStatus.REJECTED);

            Exchange exchange = new Exchange();
            exchange.setOffer(offer);
            exchange.setItem(item);
            exchange.setOwner(item.getOwner());
            exchange.setProposer(offer.getProposer());
            return exchangeRepository.save(exchange);
        } finally {
            redisLockService.unlock(lockKey, lockToken);
        }
    }

    @Transactional
    public Offer rejectOffer(String ownerUsername, Long offerId) {
        Offer offer = offerRepository.findByIdWithItemAndProposer(offerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));
        Item item = offer.getItem();
        User owner = userRepository.findByUsername(ownerUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!item.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can reject offers");
        }
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Offer is not pending");
        }
        offer.setStatus(OfferStatus.REJECTED);
        return offer;
    }
}
