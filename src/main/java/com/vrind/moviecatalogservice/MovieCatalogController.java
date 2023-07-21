package com.vrind.moviecatalogservice;

import com.vrind.moviecatalogservice.model.CatalogItem;
import com.vrind.moviecatalogservice.model.Movie;
import com.vrind.moviecatalogservice.model.Rating;
import com.vrind.moviecatalogservice.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webBuilder;

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        UserRating ratings = restTemplate.getForObject("http://localhost:8082/ratings/users/" + userId, UserRating.class);
        return ratings.getUserRatings().stream().map(rating -> {
            /* using RestTemplate
            Movie movie = restTemplate.getForObject("localhost/8081/movies/"+rating.getMovieId(), Movie.class);
            */
            // using WebClient.Builder
            Movie movie = webBuilder.build()
                    .get()
                    .uri("http://localhost:8081/movies/"+rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();

            return new CatalogItem(movie.getMovieName(), "DESC", rating.getRating());
        }).collect(Collectors.toList());
    }
}
