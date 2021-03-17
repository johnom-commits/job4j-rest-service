package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PersonRepository repository;

    @Captor
    ArgumentCaptor<Person> personArgumentCaptor;

    @Test
    public void whenFindAllPersonsThenGetListPersons() throws Exception {
        List<Person> persons = List.of(
                Person.of(1, "Ivan", "123"),
                Person.of(2, "Petr", "123"),
                Person.of(3, "Roman", "123")
        );
        String jsonOut = mapper.writeValueAsString(persons);

        when(repository.findAll()).thenReturn(persons);

        mockMvc.perform(get("/person/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonOut));
    }

    @Test
    public void whenFindByIdPersonThenReturnPerson() throws Exception {
        Person person = Person.of(1, "johnom", "123");
        String jsonOut = mapper.writeValueAsString(person);

        when(repository.findById(1)).thenReturn(java.util.Optional.of(person));
        mockMvc.perform(get("/person/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonOut));
    }

    @Test
    public void whenCreateNewPersonThenGetPerson() throws Exception {
        Person person = Person.of(1, "johnom", "123");
        String jsonOut = mapper.writeValueAsString(person);

        mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonOut))
                .andExpect(status().isCreated());

        verify(repository, times(1)).save(personArgumentCaptor.capture());
        Person personArg = personArgumentCaptor.getValue();
        assertEquals("johnom", personArg.getLogin());
        assertEquals("123", personArg.getPassword());
    }

    @Test
    public void whenDeletePersonThenGetOkStatus() throws Exception {
        mockMvc.perform(delete("/person/1"))
                .andExpect(status().isOk());

        verify(repository, times(1)).delete(any());
    }

    @Test
    public void whenUpdateExistingPersonThenGetOkStatus() throws Exception {
        String updatedPerson = mapper.writeValueAsString(Person.of(1, "john", "123"));

        mockMvc.perform(put("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPerson))
                .andExpect(status().isOk());

        verify(repository, times(1)).save(any());
    }

    @Test
    public void whenFindMissingPersonByIdThenReturnError() throws Exception {
        String jsonOut = mapper.writeValueAsString(new Person());

        mockMvc.perform(get("/person/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonOut));

        verify(repository, times(1)).findById(anyInt());
    }
}