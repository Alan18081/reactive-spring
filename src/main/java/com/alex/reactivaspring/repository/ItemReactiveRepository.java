package com.alex.reactivaspring.repository;

import com.alex.reactivaspring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

    Flux<Item> findByDescriptionLike(String description);

    Mono<Item> findByDescription(String description);

}
