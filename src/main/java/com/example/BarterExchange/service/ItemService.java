package com.example.BarterExchange.service;

import com.example.BarterExchange.domain.Item;
import com.example.BarterExchange.domain.User;
import com.example.BarterExchange.repository.ItemRepository;
import com.example.BarterExchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Item createItem(String ownerUsername, String title, String description) {
        User owner = userRepository.findByUsername(ownerUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Item item = new Item();
        item.setOwner(owner);
        item.setTitle(title);
        item.setDescription(description);
        return itemRepository.save(item);
    }
}
