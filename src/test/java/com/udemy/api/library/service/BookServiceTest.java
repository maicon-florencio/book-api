package com.udemy.api.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest(){
    Long id =1l;

    var book = createdValidBook();
    book.setId(id);
    Mockito.when(bookDao.findById(id)).thenReturn(Optional.of(book));

    Optional<Book>  foundBook = bookService.getByid(id);

    assertThat(foundBook.isPresent()).isTrue(); 
    assertThat(foundBook.get().getId()).isEqualTo(id);
    assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
    assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retorna vazio quando id for inexistente")
    public void bookNotFoundIdTest(){
    Long id =1l;

    Mockito.when(bookDao.findById(id)).thenReturn(Optional.empty());

    Optional<Book>  book = bookService.getByid(id);

    assertThat(book.isPresent()).isFalse(); 
    }

    @Test
    @DisplayName("Deve obter atualizar um livro existente")
    public void updateBookFoundTest(){
    Long id=1l;
    //livro para atualizar
    var bookUpdating = Book.builder().id(id).build();

    var bookUpdated = createdValidBook();
    bookUpdated.setId(id);


    Mockito.when(bookDao.save(bookUpdating)).thenReturn(bookUpdated);
        var book = bookService.update(bookUpdating);

    
    assertThat(book.getId()).isEqualTo(bookUpdated.getId());
    assertThat(book.getAuthor()).isEqualTo(bookUpdated.getAuthor());
    assertThat(book.getTitle()).isEqualTo(bookUpdated.getTitle());
    assertThat(book.getIsbn()).isEqualTo(bookUpdated.getIsbn());
    }

    @Test
    @DisplayName("deve deletar um livro")
    public void deleteBookTest(){

        var book = Book.builder().id(1l).build();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow( ()-> bookService.delete(book));

        Mockito.verify(bookDao, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest(){

        var book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class,()-> bookService.delete(book));


        Mockito.verify(bookDao, Mockito.never()).delete(book);
    
    }

    @Test
    @DisplayName("deve ocorrer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest(){

        var book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class,()-> bookService.update(book));


        Mockito.verify(bookDao, Mockito.never()).save(book);
    
    }

}
