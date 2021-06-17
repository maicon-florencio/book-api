package com.udemy.api.library.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.udemy.api.library.dao.BookDao;
import com.udemy.api.library.model.Book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookDaoTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookDao bookDao;

    @Test
    @DisplayName("Deve retorna verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists(){
        String isbn ="123";

        var book = createNewBook(isbn);
        entityManager.persist(book);

        boolean existe = bookDao.existsByIsbn(isbn);

        assertThat(existe).isTrue();

    }

    private Book createNewBook(String isbn) {
        return Book.builder().title("As aventuras de pimpe").author("AnyBody").isbn(isbn).build();
    }

    @Test
    @DisplayName("Deve retorna falso quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoenstExists(){
        String isbn ="123";

        boolean existe = bookDao.existsByIsbn(isbn);

        assertThat(existe).isFalse();

    }

    @Test
    @DisplayName("deve obter um livro por id")
    public void findByIdTest(){
        var book = createNewBook("123");
        entityManager.persist(book);

        var foundBook = bookDao.findById(book.getId());

        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        var book = createNewBook("123");

        var bookSAve= bookDao.save(book);
        assertThat(bookSAve.getId()).isNotNull();
    }
    @Test
    @DisplayName("deve deletar um livro")
    public void deleteBookTest(){

        var book = createNewBook("123");
        entityManager.persist(book);

        var foundBook = entityManager.find(Book.class, book.getId());
        bookDao.delete(foundBook);

        var deleteBook = entityManager.find(Book.class, book.getId());

        assertThat(deleteBook).isNull();

    }

}
