package com.udemy.api.library.service.impl;

import java.util.Optional;

import com.udemy.api.library.dao.BookDao;
import com.udemy.api.library.exception.BusinessException;
import com.udemy.api.library.model.Book;
import com.udemy.api.library.service.BookService;

import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookDao bookDao;

    public BookServiceImpl(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public Book save(Book book) {
        if (bookDao.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado");
        }
        return bookDao.save(book);
    }

    @Override
    public Optional<Book> getByid(Long id) {
        return bookDao.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book.getId() == null){
            throw new IllegalArgumentException("Book id cant be null.");
        }
       bookDao.delete(book);

    }

	@Override
	public Book update(Book book) {
        if(book.getId() == null){
            throw new IllegalArgumentException("Book id cant be null.");
        }
		return bookDao.save(book);
	}
    
    
}
