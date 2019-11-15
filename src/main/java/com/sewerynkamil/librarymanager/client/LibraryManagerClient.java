package com.sewerynkamil.librarymanager.client;

import com.google.gson.Gson;
import com.sewerynkamil.librarymanager.dto.BookDto;
import com.sewerynkamil.librarymanager.dto.RequestJwtDto;
import com.sewerynkamil.librarymanager.dto.UserDto;
import com.sewerynkamil.librarymanager.dto.WolneLekturyAudiobookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author Kamil Seweryn
 */

@Component
public class LibraryManagerClient {
    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    private String jwttoken;

    public String createAuthenticationToken(RequestJwtDto authenticationRequest){
        Gson gson = new Gson();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonContent = gson.toJson(authenticationRequest);
        HttpEntity<String> request = new HttpEntity<>(jsonContent, headers);
        return restTemplate.postForObject("http://localhost:8080/v1/login", request, String.class);
    }

    public void registerUser(UserDto userDto) {
        Gson gson = new Gson();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonContent = gson.toJson(userDto);
        HttpEntity<String> request = new HttpEntity<>(jsonContent, headers);
        restTemplate.postForObject("http://localhost:8080/v1/register", request, UserDto.class);
    }

    public List<BookDto> getAllBooks() {
        headers.set(HttpHeaders.AUTHORIZATION, jwttoken);
        HttpEntity<String> request = new HttpEntity<>("authentication", headers);
        ResponseEntity<BookDto[]> response =
                restTemplate.exchange(
                        "http://localhost:8080/v1/books",
                        HttpMethod.GET,
                        request,
                        BookDto[].class);
        List<BookDto> responseList = Arrays.asList(response.getBody());
        return responseList;
    }

    public List<WolneLekturyAudiobookDto> getAllAudiobooks() {
        headers.set(HttpHeaders.AUTHORIZATION, jwttoken);
        HttpEntity<String> request = new HttpEntity<>("authentication", headers);
        ResponseEntity<WolneLekturyAudiobookDto[]> response =
                restTemplate.exchange(
                        "http://localhost:8080/v1/books/audiobooks",
                        HttpMethod.GET,
                        request,
                        WolneLekturyAudiobookDto[].class);
        List<WolneLekturyAudiobookDto> responseList = Arrays.asList(response.getBody());
        return responseList;
    }

    public boolean isUserExist(String email) {
        return restTemplate.getForObject("http://localhost:8080/v1/users/exist/" + email, Boolean.class);
    }

    public void setJwttoken(String jwttoken) {
        this.jwttoken = jwttoken;
    }
}