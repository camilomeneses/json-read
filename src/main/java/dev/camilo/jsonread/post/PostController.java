package dev.camilo.jsonread.post;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/post")
public class PostController {

  private final PostRepository postRepository;

  public PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @QueryMapping
  List<Post> findAllPosts() {
    return postRepository.findAll();
  }

  @QueryMapping
  Post findPostById(@Argument String id) {
    return postRepository.findById(id).orElseThrow();
  }

}
