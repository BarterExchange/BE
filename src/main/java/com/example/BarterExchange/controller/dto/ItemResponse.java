package com.example.BarterExchange.controller.dto;

import com.example.BarterExchange.domain.Item;
import com.example.BarterExchange.domain.ItemStatus;
import lombok.Getter;

@Getter
public class ItemResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final ItemStatus status;

    public ItemResponse(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.status = item.getStatus();
    }
}
