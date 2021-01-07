package com.udemy.api.library.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.api.library.dto.BookDTO;
import com.udemy.api.library.exception.BusinessException;
import com.udemy.api.library.model.Book;
import com.udemy.api.library.service.BookService;

import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import lombok.var;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    public static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mock;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve criar um livro")
    public void createBookTest() throws Exception {

        var bookTest = createNewBookDto();
        var bookSave = Book.builder().id(10L).title("MEU AVO").author("zezo").isbn("123123").build();
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(bookSave);
        String jsonTest = new ObjectMapper().writeValueAsString(bookTest);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(jsonTest);

        mock.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookTest.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookTest.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookTest.getIsbn()));

    }

    @Test
    @DisplayName("Nao deve criar um livro sem dados suficiente")
    public void createBookFailTest() throws Exception {

        String jsonTest = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonTest);

        mock.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors",hasSize(3)));

    }

    @Test
    @DisplayName("Deve lancar erro ao tentar cadastrar um Isbn já utilizado")
    public void createBookWithDuplicatedIsbn() throws Exception {
       
        var dto = createNewBookDto();
        String jsonTest = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Isbn já cadastrado";

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
        .willThrow(new BusinessException(mensagemErro));

        var request = MockMvcRequestBuilders.
        post(BOOK_API).
            contentType(MediaType.APPLICATION_JSON).
            accept(MediaType.APPLICATION_JSON).content(jsonTest);

          
        mock.perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("errors", hasSize(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(mensagemErro));


    }

    private BookDTO createNewBookDto() {
        return BookDTO.builder().title("MEU AVO").author("zezo").isbn("123123").build();
    }
    
}
