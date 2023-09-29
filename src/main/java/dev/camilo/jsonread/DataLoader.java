package dev.camilo.jsonread;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.asm.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.camilo.jsonread.post.Post;
import dev.camilo.jsonread.post.PostRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DataLoader implements CommandLineRunner {

  private final PostRepository postRepository;
  private final ObjectMapper objectMapper;

  public DataLoader(PostRepository postRepository, ObjectMapper objectMapper) {
    this.postRepository = postRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void run(String... args) throws Exception {

    // List of Post define
    List<Post> posts = new ArrayList<>();

    // JsonNode define
    JsonNode json;

    // Read JSON data from file
    try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/blog-post.json")) {
      json = objectMapper.readValue(inputStream, JsonNode.class);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read JSON data " + e);
    }

    // Get edges from JSON data
    JsonNode edges = getEdges(json);

    // Iterate over edges
    for (JsonNode edge : edges) {
      posts.add(createPostFromNode(edge));
    }

    // save posts at repository
    postRepository.saveAll(posts);
  }

  // Create Post from JsonNode
  private Post createPostFromNode(JsonNode edge) {

    JsonNode node = edge.get("node");
    String id = node.get("id").asText();
    String title = node.get("title").asText();
    String slug = node.get("slug").asText();
    String date = node.get("date").asText();
    int timeToRead = node.get("timeToRead").asInt();
    String tags = extractTags(node);
    return new Post(id, title, slug, LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy")), timeToRead, tags,
        null);
  }

  // extract tags from JsonNode
  private String extractTags(JsonNode node) {
    JsonNode tags = node.get("tags");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tags.size(); i++) {
      sb.append(tags.get(i).get("title").asText());
      if (i < tags.size() - 1) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  // Get edges from JSON data method
  private JsonNode getEdges(JsonNode json) {
    return Optional.ofNullable(json)
        .map(j -> j.get("data"))
        .map(j -> j.get("allPost"))
        .map(j -> j.get("edges"))
        .orElseThrow(() -> new IllegalArgumentException("Invalid JSON Object"));
  }

}
