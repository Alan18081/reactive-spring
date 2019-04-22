package com.alex.reactivaspring.router;

import com.alex.reactivaspring.constants.ItemConstants;
import com.alex.reactivaspring.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(
                    GET(ItemConstants.ITEMS_FUNCTIONAL_END_POINT).and(accept(MediaType.APPLICATION_JSON)),
                    itemsHandler::getAllItems
                )
                .andRoute(
                    GET(ItemConstants.ITEMS_FUNCTIONAL_END_POINT + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                    itemsHandler::getOneItemById
                )
                .andRoute(
                    POST(ItemConstants.ITEMS_FUNCTIONAL_END_POINT).and(accept(MediaType.APPLICATION_JSON)),
                    itemsHandler::createItem
                )
                .andRoute(
                    DELETE(ItemConstants.ITEMS_FUNCTIONAL_END_POINT + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                    itemsHandler::deleteItem
                )
                .andRoute(
                    PUT(ItemConstants.ITEMS_FUNCTIONAL_END_POINT + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                    itemsHandler::updateItem
                );
    }

}
