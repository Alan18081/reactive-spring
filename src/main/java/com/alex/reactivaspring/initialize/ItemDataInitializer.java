package com.alex.reactivaspring.initialize;

import com.alex.reactivaspring.document.Item;
import com.alex.reactivaspring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV",400.0),
                new Item(null, "Apple Watch",299.99),
                new Item("ABC", "Beats Headphones", 149.99));
    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(data()))
            .flatMap(itemReactiveRepository::save)
            .thenMany(itemReactiveRepository.findAll())
            .subscribe(item -> {
                System.out.println("Inserted: " + item);
            });
    }
}
