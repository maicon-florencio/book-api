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

        var book = Book.builder().title("As aventuras de pimpe").author("AnyBody").isbn(isbn).build();
        entityManager.persist(book);

        boolean existe = bookDao.existsByIsbn(isbn);

        assertThat(existe).isTrue();

    }

    @Test
    @DisplayName("Deve retorna falso quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoenstExists(){
        String isbn ="123";

        boolean existe = bookDao.existsByIsbn(isbn);

        assertThat(existe).isFalse();

    }

}
