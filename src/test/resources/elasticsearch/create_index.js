curl -XPOST 'localhost:9200/movies' -d
{
    "mappings": {
    "movie": {
        "properties": {
            "title": {
                "type":     "text",
                    "analyzer": "english"
            }
        }
    }
}
}