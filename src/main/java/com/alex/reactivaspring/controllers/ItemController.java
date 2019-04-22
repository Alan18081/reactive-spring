package com.alex.reactivaspring.controllers;

import com.alex.reactivaspring.constants.ItemConstants;
import com.alex.reactivaspring.document.Item;
import com.alex.reactivaspring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ItemConstants.ITEMS_END_POINT)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ItemConstants.ITEMS_END_POINT + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
        return itemReactiveRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ItemConstants.ITEMS_END_POINT)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ItemConstants.ITEMS_END_POINT + "/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ItemConstants.ITEMS_END_POINT + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
        return itemReactiveRepository.findById(id)
                .flatMap(foundItem -> {
                    BeanUtils.copyProperties(item, foundItem);
                    return itemReactiveRepository.save(foundItem);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
