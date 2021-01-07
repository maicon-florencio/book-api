package com.udemy.api.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.udemy.api.library.dao.BookDao;
import com.udemy.api.library.exception.BusinessException;
import com.udemy.api.library.model.Book;
import com.udemy.api.library.service.impl.BookServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;


    @MockBean
    BookDao bookDao;

    @BeforeEach
    public void setUp(){
        this.bookService = new BookServiceImpl(bookDao);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        var book = createdValidBook();
        Mockito.when(bookDao.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(bookDao.save(book)).
            thenReturn(Book.builder().id(1l).title("As aventuras de pimpe").
          author("AnyBody").
          isbn("4321").
        build());

        Book bookSalvo = bookService.save(book);

        assertThat(bookSalvo.getId()).isNotNull();
        assertThat(bookSalvo.getTitle()).isEqualTo("As aventuras de pimpe");
        assertThat(bookSalvo.getAuthor()).isEqualTo("AnyBody");
        assertThat(bookSalvo.getIsbn()).isEqualTo("4321");

    }

    Book createdValidBook() {
        return Book.builder().
        title("As aventuras de pimpe").
        author("AnyBody").
        isbn("4321").
        build();
    }


    @Test
    @DisplayName("deve lançar erro de negocio tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveBookWithDuplicateISBN(){
        Book book = createdValidBook();

        Mockito.when(bookDao.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable excepiton =  Assertions.catchThrowable(()-> bookService.save(book));
        assertThat(excepiton).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado");

        Mockito.verify(bookDao, Mockito.never()).save(book);

    }
}
