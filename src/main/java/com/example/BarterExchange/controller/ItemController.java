package com.example.BarterExchange.controller;

import com.example.BarterExchange.controller.dto.ItemCreateRequest;
import com.example.BarterExchange.controller.dto.ItemResponse;
import com.example.BarterExchange.domain.Item;
import com.example.BarterExchange.domain.User;
import com.example.BarterExchange.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponse createItem(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody ItemCreateRequest request
    ) {
        Item item = itemService.createItem(user.getUsername(), request.getTitle(), request.getDescription());
        return new ItemResponse(item);
    }
}
