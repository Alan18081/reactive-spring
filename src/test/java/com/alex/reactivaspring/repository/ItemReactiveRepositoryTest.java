package com.alex.reactivaspring.repository;

import com.alex.reactivaspring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
        new Item(null, "Samsung TV",400.0),
        new Item(null, "Apple Watch",299.99),
        new Item("ABC", "Beats Headphones", 149.99)
    );

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is: " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getItemByID() {
        Item mockItem = new Item("ABC", "Beats Headphones", 149.99);
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNext(mockItem)
                .verifyComplete();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescriptionLike("Headphones"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> itemMono = itemReactiveRepository.save(item);
        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals(item.getDescription()))
                .verifyComplete();

    }

    @Test
    public void updateItem() {
        double newPrice = 520.00;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("Apple Watch")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == newPrice)
                .verifyComplete();
    }

    @Test
    public void deleteItemById() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id));

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("Apple Watch")
                .flatMap(item -> itemReactiveRepository.delete(item));

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectNextCount(2)
                .verifyComplete();
    }
}
