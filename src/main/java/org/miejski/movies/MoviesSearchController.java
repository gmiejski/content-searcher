package org.miejski.movies;

import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MoviesSearchController {

    @Autowired
    private MoviesSearchRepository moviesSearchRepository;

//    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public void get() {
//        moviesSearchRepository.getAll(value);
//    }

    @RequestMapping(value = "/movies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Movie>> search(@RequestParam(value = "word", required = false) String searchKey) {
        List<Movie> moviesFound = moviesSearchRepository.getAll(searchKey);
        return ResponseEntity.ok(moviesFound);
    }

    @PostMapping(value = "/")
    public ResponseEntity<Void> createNew() {
        return ResponseEntity.ok().build();
    }
}
