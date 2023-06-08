package com.mycompany.springboot.restfulapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.springboot.restfulapi.model.response.AddressResponse;
import com.mycompany.springboot.restfulapi.model.request.UpdateAddressRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.mycompany.springboot.restfulapi.entity.*;
import com.mycompany.springboot.restfulapi.model.request.CreateAddressRequest;
import com.mycompany.springboot.restfulapi.model.response.WebResponse;
import com.mycompany.springboot.restfulapi.repository.*;
import com.mycompany.springboot.restfulapi.security.BCrypt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("testId");
        contact.setUser(user);
        contact.setFirstName("Arine");
        contact.setLastName("Fitriani");
        contact.setEmail("arine@example.com");
        contact.setPhone("039285098");
        contactRepository.save(contact);
    }

    @Test
    void createAddressBadRequest() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                post("/api/contacts/testId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createAddressSuccess() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Jalan");
        request.setCity("Jakarta");
        request.setProvince("Jakarta");
        request.setCountry("Indonesia");
        request.setPostalCode("12345");

        mockMvc.perform(
                post("/api/contacts/testId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());
            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/testId/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("testId").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Jalan");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setCountry("Indonesia");
        address.setPostalCode("123123");
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/testId/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(address.getId(), response.getData().getId());
            assertEquals(address.getStreet(), response.getData().getStreet());
            assertEquals(address.getCity(), response.getData().getCity());
            assertEquals(address.getProvince(), response.getData().getProvince());
            assertEquals(address.getCountry(), response.getData().getCountry());
            assertEquals(address.getPostalCode(), response.getData().getPostalCode());
        });
    }

    @Test
    void updateAddressBadRequest() throws Exception {
        Contact contact = contactRepository.findById("testId").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Jalan");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setCountry("Indonesia");
        address.setPostalCode("123123");
        addressRepository.save(address);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                put("/api/contacts/testId/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressNotFound() throws Exception {
        Contact contact = contactRepository.findById("testId").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Jalan");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setCountry("Indonesia");
        address.setPostalCode("123123");
        addressRepository.save(address);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setStreet("Jalan Raya");
        request.setCity("Tangerang");
        request.setProvince("Banten");
        request.setCountry("Indonesia");
        request.setPostalCode("12345");

        mockMvc.perform(
                put("/api/contacts/testId/addresses/testId")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("testId").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Jalan");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setCountry("Indonesia");
        address.setPostalCode("123123");
        addressRepository.save(address);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setStreet("Jalan Raya");
        request.setCity("Tangerang");
        request.setProvince("Banten");
        request.setCountry("Indonesia");
        request.setPostalCode("12345");

        mockMvc.perform(
                put("/api/contacts/testId/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());
            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void deleteAddressNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contacts/testId/addresses/test123")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("testId").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Jalan");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setCountry("Indonesia");
        address.setPostalCode("123123");
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contacts/testId/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("OK", response.getData());

            assertFalse(addressRepository.existsById(address.getId()));
        });
    }

    @Test
    void listAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/wrongId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void listAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("testId").orElseThrow();

        for (int i = 0; i < 5; i++) {
            Address address = new Address();
            address.setId("test" + i);
            address.setContact(contact);
            address.setStreet("Jalan" + i);
            address.setCity("Jakarta");
            address.setProvince("DKI");
            address.setCountry("Indonesia");
            address.setPostalCode("123123");
            addressRepository.save(address);
        }

        mockMvc.perform(
                get("/api/contacts/testId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(5, response.getData().size());
        });
    }
}