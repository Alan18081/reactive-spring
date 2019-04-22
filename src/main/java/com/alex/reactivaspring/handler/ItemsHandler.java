package com.alex.reactivaspring.handler;

import com.alex.reactivaspring.document.Item;
import com.alex.reactivaspring.repository.ItemReactiveRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemsHandler {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItemById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono.flatMap(item -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(item))
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Item.class)
                .flatMap(item -> itemReactiveRepository.save(item))
                .flatMap(item -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(item))
                );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return itemReactiveRepository.deleteById(id)
                .flatMap(item -> ServerResponse
                        .accepted()
                        .contentType(MediaType.APPLICATION_JSON).build()
                );
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.bodyToMono(Item.class)
                .flatMap(item -> itemReactiveRepository.findById(id)
                        .map(foundItem -> {
                            BeanUtils.copyProperties(item, foundItem);
                            return foundItem;
                        })
                        .flatMap(updateItem -> itemReactiveRepository.save(updateItem))
                        .flatMap(savedItem -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromObject(item))
                        )
                        .switchIfEmpty(ServerResponse.notFound().build())
                );
    }

}
