package org.miejski.movies;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MoviesBatchSave {

    private MoviesSearchRepository moviesSearchRepository;

    @Autowired
    public MoviesBatchSave(MoviesSearchRepository moviesSearchRepository) {
        this.moviesSearchRepository = moviesSearchRepository;
    }

    private static final List<String> TAGS = parseTags();
    private static final String DATA_FILE = "src/main/resources/data/u.item";
    private static final String DATA_IMPORTED_LOCK = "src/main/resources/data/imported.lock";

    private Movie toMovie(String line) {
        List<String> datas = Arrays.asList(line.split("\\|"));
        List<String> tags = extractTags(datas.subList(5, datas.size()));
        return new Movie(datas.get(0), datas.get(1), tags, datas.get(4));
    }

    private List<String> extractTags(List<String> strings) {
        ArrayList<String> tagsFound = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            if (Objects.equals(strings.get(i), "1")) {
                tagsFound.add(TAGS.get(i));
            }
        }
        return tagsFound;
    }

    @PostConstruct
    public void createIndex() {
        if (!Files.exists(Paths.get(DATA_IMPORTED_LOCK))) {
            try {
                List<String> movies = Files.readAllLines(Paths.get(DATA_FILE), Charset.forName("ISO-8859-1"));
                List<Movie> collect = movies.stream().map(this::toMovie)
                        .collect(Collectors.toList());
                moviesSearchRepository.saveAll(collect);
                Files.createFile(Paths.get(DATA_IMPORTED_LOCK));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> parseTags() {
        return Arrays.stream("unknown | Action | Adventure | Animation | Children's | Comedy | Crime | Documentary | Drama | Fantasy | Film-Noir | Horror | Musical | Mystery | Romance | Sci-Fi | Thriller | War | Western".split("\\|"))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
