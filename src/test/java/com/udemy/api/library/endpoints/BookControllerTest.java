package com.udemy.api.library.endpoints;

import static org.hamcrest.Matchers.hasSize;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.api.library.dto.BookDTO;
import com.udemy.api.library.exception.BusinessException;
import com.udemy.api.library.model.Book;
import com.udemy.api.library.service.BookService;

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
    public void createBookInvalidoTest() throws Exception {

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

    @Test
    @DisplayName("deve obter informações de um livro")
    public void getBookDetailsTest() throws Exception {
      Long id =1l;

      var book = Book.builder()
                .id(id).
                    title(createNewBookDto().getTitle())
                    .author(createNewBookDto().getAuthor())
                    .isbn(createNewBookDto().getIsbn()).build();

      BDDMockito.given(bookService.getByid(id)).willReturn(Optional.of(book));

    var request = MockMvcRequestBuilders.get(BOOK_API.concat("/"+ id)).accept(MediaType.APPLICATION_JSON);

    mock.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
    .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBookDto().getTitle()))
    .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBookDto().getAuthor()))
    .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBookDto().getIsbn()));


    }

    @Test
    @DisplayName("deve retorna resource not found quando livro procurado não existir")
    public void bookDontFound() throws Exception {

      BDDMockito.given(bookService.getByid(Mockito.anyLong())).willReturn(Optional.empty());

    var request = MockMvcRequestBuilders.get(BOOK_API.concat("/"+ 1)).accept(MediaType.APPLICATION_JSON);

    mock.perform(request).
    andExpect(MockMvcResultMatchers.status().isNotFound());


    }
    @Test
    @DisplayName("deve deletar um livro")
    public void deleteBookTest() throws Exception{
        long id =1l;

        BDDMockito.given(bookService.getByid(Mockito.anyLong())).
                willReturn(Optional.of(Book.builder().id(id).build()));

        var request = MockMvcRequestBuilders.delete(BOOK_API.concat("/"+ id)).accept(MediaType.APPLICATION_JSON);

        mock.perform(request).
                 andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    @Test
    @DisplayName("deve retorna resourse not found quando nao encontrar um livro para deletar")
     public void deleteBookNotFound() throws Exception{

        BDDMockito.given(bookService.getByid(Mockito.anyLong())).
                willReturn(Optional.empty());

        var request = MockMvcRequestBuilders.delete(BOOK_API.concat("/"+ "1")).accept(MediaType.APPLICATION_JSON);

        mock.perform(request).
                 andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    @DisplayName("deve atualizar um livro existente")
    public void updateBookTest()throws Exception{
        Long id=1l;
        String json =  new ObjectMapper().writeValueAsString(createNewBookDto());

        var updatingBook = Book.builder().id(id).title("some title").author("some autor").isbn("321").build();
        BDDMockito.given(bookService.getByid(id)).willReturn(Optional.of(updatingBook));
        
        var  updatedBook = Book.builder().id(id).title("MEU AVO").author("zezo").isbn("321").build();
        BDDMockito.given(bookService.update(updatingBook)).willReturn(updatedBook);

        var request = MockMvcRequestBuilders.
        put(BOOK_API.concat("/"+ id))
        .content(json)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

        mock.perform(request).
         andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBookDto().getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBookDto().getAuthor()))
        .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("321"));

    }

    @Test
    @DisplayName("deve retorna 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest()throws Exception{

        String json = new ObjectMapper().writeValueAsString(createNewBookDto());
       
        BDDMockito.given(bookService.getByid(Mockito.anyLong())).
        willReturn(Optional.empty());

        var request = MockMvcRequestBuilders.
            put(BOOK_API.concat("/"+ "1")).accept(MediaType.APPLICATION_JSON)
            .content(json)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mock.perform(request).
                 andExpect(MockMvcResultMatchers.status().isNotFound());

    }


    private BookDTO createNewBookDto() {
        return BookDTO.builder().title("MEU AVO").author("zezo").isbn("123123").build();
    }
    
}
