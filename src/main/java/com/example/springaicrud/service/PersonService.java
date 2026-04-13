package com.example.springaicrud.service;

import com.example.springaicrud.dto.PersonDTO;
import com.example.springaicrud.entity.Person;
import com.example.springaicrud.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;          // ✅ Fix 1: Add this import
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j                                      // ✅ Fix 1: Add @Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PersonService {

    private final PersonRepository personRepository;
    private final FileStorageService fileStorageService;

    private static final String BASE_URL = "http://localhost:8080";

    public PersonDTO createPerson(String name, String mobileNo,
                                  String address, MultipartFile image) throws IOException {
        if (personRepository.existsByMobileNo(mobileNo)) {
            throw new RuntimeException("Mobile number already exists: " + mobileNo);
        }

        boolean hasImage = image != null && !image.isEmpty(); // ✅ Fix 2

        Person person = Person.builder()
                .name(name)
                .mobileNo(mobileNo)
                .address(address)
                .image(hasImage ? image.getBytes() : null)
                .imageName(hasImage ? image.getOriginalFilename() : null)
                .imageType(hasImage ? image.getContentType() : null)
                .imagePath(hasImage ? fileStorageService.storeImage(image) : null)
                .build();

        Person saved = personRepository.saveAndFlush(person);
        Long generatedId = saved.getId();

        log.info("Person saved with ID: {}", generatedId);


        if (hasImage) {
            String imageUrl = BASE_URL
                    + "/api/persons/"
                    + generatedId
                    + "/image";

            personRepository.updateImageUrl(generatedId, imageUrl);
            saved.setImageUrl(imageUrl);
        }

        return toDTO(saved);
    }

    public List<PersonDTO> getAllPersons() {
        return personRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PersonDTO getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        return toDTO(person);
    }

    public PersonDTO updatePerson(Long id, String name, String mobileNo,
                                  String address, MultipartFile image) throws IOException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        person.setName(name);
        person.setMobileNo(mobileNo);
        person.setAddress(address);

        if (image != null && !image.isEmpty()) {
            person.setImage(image.getBytes());
            person.setImageName(image.getOriginalFilename());
            person.setImageType(image.getContentType());
            fileStorageService.deleteIfExists(person.getImagePath());
            person.setImagePath(fileStorageService.storeImage(image));

            String imageUrl = BASE_URL + "/api/persons/"
                    + id + "/image";
            person.setImageUrl(imageUrl);
        }

        Person updated = personRepository.save(person);
        return toDTO(updated);
    }

    public void deletePerson(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        fileStorageService.deleteIfExists(person.getImagePath());
        personRepository.deleteById(id);
    }

    public Person getPersonEntityById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
    }

    private PersonDTO toDTO(Person person) {
        String base64Image = null;
        if (person.getImage() != null) {
            base64Image = Base64.getEncoder().encodeToString(person.getImage());
        }
        return PersonDTO.builder()
                .id(person.getId())
                .name(person.getName())
                .mobileNo(person.getMobileNo())
                .address(person.getAddress())
                .imageName(person.getImageName())
                .imageType(person.getImageType())
                .imageBase64(base64Image)
                .imageUrl(person.getImageUrl())
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .build();
    }
}