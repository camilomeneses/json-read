package dev.camilo.jsonread.post;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

public record Post(
  @Id
  String id,
  String title,
  String slug,
  LocalDate date,
  int timeToRead,
  String tags,
  @Version
  Integer version
  ) {}
