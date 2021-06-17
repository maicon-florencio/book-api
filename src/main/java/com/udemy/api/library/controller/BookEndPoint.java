package com.udemy.api.library.controller;

import com.udemy.api.library.dto.BookDTO;
import com.udemy.api.library.error.ApiErrors;
import com.udemy.api.library.exception.BusinessException;
import com.udemy.api.library.model.Book;
import com.udemy.api.library.service.BookService;


import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/books")
public class BookEndPoint {

    private BookService bookService;
    private ModelMapper modelMapper;

    public BookEndPoint(BookService bookService, ModelMapper mapper) {
        this.bookService = bookService;
        this.modelMapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO save(@RequestBody  BookDTO livro) {
        var bookNovo = modelMapper.map(livro, Book.class);
        bookNovo = bookService.save(bookNovo);
        return modelMapper.map(bookNovo, BookDTO.class);

    }
    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id){
        return 
            bookService.
                getByid(id).
                    map(book -> modelMapper.map(book,BookDTO.class))
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }
   
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        var book = bookService.getByid(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
            bookService.delete(book);
                    
    }

    @PutMapping("{id}")
    public BookDTO Update(@PathVariable Long id, BookDTO dto){
        var book = bookService.getByid(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book = bookService.update(book);
        return modelMapper.map( book, BookDTO.class);
    } 

    

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);

    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);

    }

}
