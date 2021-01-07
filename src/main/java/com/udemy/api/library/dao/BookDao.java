package com.udemy.api.library.dao;

import com.udemy.api.library.model.Book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDao extends JpaRepository<Book,Long>{

	boolean existsByIsbn(String isbn);
    
}
