package com.alex.reactivaspring.handler;

import com.alex.reactivaspring.constants.ItemConstants;
import com.alex.reactivaspring.document.Item;
import com.alex.reactivaspring.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV",400.0),
                new Item(null, "Apple Watch",299.99),
                new Item("ABC", "Beats Headphones", 149.99));
    }

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted: " + item);
                })
                .blockLast();
    }

    @Test
    public void getAllItems() {
        client.get().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(3);
    }

    @Test
    public void getAllItems_approach2() {
        client.get().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(3)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        assertNotNull(item.getId());
                    });
                });
    }

    @Test
    public void getAllItems_approach3() {
        Flux<Item> flux = client.get().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        client.get().uri(ItemConstants.ITEMS_END_POINT.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 149.99);
    }

    @Test
    public void getOneItem_notFound() {
        client.get().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {
        Item item = new Item(null, "Iphone X", 999.99);

        client.post().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo(item.getDescription())
                .jsonPath("$.price").isEqualTo(item.getPrice());
    }

    @Test
    public void deleteItem() {
        client.delete().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem() {
        Item item = new Item(null, "Some other beats", 149.99);

        client.put().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.description", item.getDescription());
    }

    @Test
    public void updateItem_notFound() {
        Item item = new Item(null, "Some other beats", 149.99);

        client.put().uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

}
