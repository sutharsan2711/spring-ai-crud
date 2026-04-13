package com.example.springaicrud.controller;

import com.example.springaicrud.dto.PersonDTO;
import com.example.springaicrud.entity.Person;
import com.example.springaicrud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class PersonController {

    private final PersonService personService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PersonDTO> createPerson(
            @RequestParam("name") String name,
            @RequestParam("mobileNo") String mobileNo,
            @RequestParam("address") String address,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        PersonDTO dto = personService.createPerson(name, mobileNo, address, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAllPersons() {
        return ResponseEntity.ok(personService.getAllPersons());
    }


    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }


    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Person person = personService.getPersonEntityById(id);
        if (person.getImage() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(person.getImageType()));
        return new ResponseEntity<>(person.getImage(), headers, HttpStatus.OK);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PersonDTO> updatePerson(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("mobileNo") String mobileNo,
            @RequestParam("address") String address,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        PersonDTO dto = personService.updatePerson(id, name, mobileNo, address, image);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.ok("Person deleted successfully with id: " + id);
    }
}