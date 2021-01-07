package com.udemy.api.library.service.impl;

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
        if(bookDao.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado");
        }
        return bookDao.save(book);
    }

   

    
    
    
    
}
