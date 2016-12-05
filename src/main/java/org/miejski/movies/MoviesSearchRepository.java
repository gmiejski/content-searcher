package org.miejski.movies;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Repository
public class MoviesSearchRepository {

    private final TransportClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public MoviesSearchRepository(TransportClient client,
                                  ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public List<Movie> getAll(String value) {
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch();
            if (!Strings.isNullOrEmpty(value)) {
                searchRequestBuilder.setQuery(new MatchQueryBuilder("title", value));
            }

            SearchResponse searchResponse = searchRequestBuilder
                    .execute().get();

            return Arrays.stream(searchResponse.getHits().hits())
                    .map(this::mapToMovie)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private Movie mapToMovie(SearchHit actionResponse) {
        try {
            Movie movie = objectMapper.readValue(actionResponse.sourceAsString(), Movie.class);
            return new Movie(actionResponse.getId(), movie.getTitle(), movie.getTags(), movie.getLink());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(List<Movie> movies) throws IOException {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        movies.stream()
                .map(this::toIndexRequest)
                .forEach(bulkRequest::add);

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            String s = bulkResponse.buildFailureMessage();
            System.err.println(s);
        }
    }

    private IndexRequestBuilder toIndexRequest(Movie movie) {
        try {
            return client.prepareIndex("movies", "movie", movie.getId())
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("title", movie.getTitle())
                            .field("link", movie.getLink())
                            .field("tags", movie.getTags())
                            .endObject()
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
