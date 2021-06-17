package com.udemy.api.library.service;

import java.util.Optional;

import com.udemy.api.library.model.Book;


public interface BookService {
  
public Book save(Book book);

public Optional<Book> getByid(Long id);

public void delete(Book book);

public Book update(Book book);

}
