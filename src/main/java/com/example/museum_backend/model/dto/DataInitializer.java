package com.example.museum_backend.model.dto;

import com.example.museum_backend.model.entity.Event;
import com.example.museum_backend.model.entity.Museum;
import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.EventType;
import com.example.museum_backend.model.enums.Location;
import com.example.museum_backend.repository.EventRepository;
import com.example.museum_backend.repository.MuseumRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final MuseumRepository museumRepository;
    private final EventRepository eventRepository;

    @PostConstruct
    public void init() {

        if (museumRepository.count() > 0 || eventRepository.count() > 0) {
            return;
        }

        // 1. Create Museum
        Museum museum = new Museum();
        museum.setName("Հայաստանի Ազգային Պատկերասրահ");
        museum = museumRepository.save(museum);

        // 2. Create Event
        Event event = new Event();
        event.setName("Մարտիրոս Սարյանի կտավները");
        event.setDescription("Այս նկարը Մարտիրոս Սարյանի ամենահայտնի խորհրդապաշտական աշխատանքներից է: Կոմպոզիցիայի կենտրոնում պատկերված է միաչքանի էակ: Ինքը` նկարիչն հանձինս այս էակի պատկերել է Փիլիսոփայի, Բանաստեղծի, Իմաստունի հավաքական մի կերպար, որն իր «ամենատես աչքով» ընդունակ է բացահայտել տիեզերքի անհայտ գաղտնիքները:\n" +
                "Սարյանը կարողացել է հավասարակշռել երևակայության թռիչքը՝ շրջապատող իրական աշխարհի պատկերմամբ: «Գիսաստղի» կոմպոզիցիան կառուցված է նկարչի արվեստին բնորոշ տարրերից` մեկհարկանի կավաշեն տնակ, տափակ կտուրի վրա նստած ֆիգուրներ, սարեր, ծառ, վիթ, ջրավազան, որի մեջ արտացոլված է գիշերային երկինքը՝ գիսաստղով: Այս ամենը արևելյան բնակավայրի պարզ, խտացված պատկերն է: Դրանով էլ առավել վառ և համոզիչ է երկու աշխարհների` հեքիաթի և ճշմարիտ իրականության համադրումը:");
        event.setEventCategory(EventCategory.ART);
        event.setEventType(EventType.MOBILE);
        event.setImageUrls(List.of(
                "http://localhost:8080/uploads/image1.jpeg",
                "http://localhost:8080/uploads/image2.jpg"
        ));
        event.setEventDate(LocalDateTime.now().plusDays(54));
        event.setPhoneNumber("99114477");
        event.setContactEmail("azgayin.patkerasrah@gmail.com");
        event.setGuidePrice(2000);
        event.setTicketPrice(5000);
        event.setLocation(Location.GYUMRI);
        event.setDuration(4);
        event.setMuseum(museum);

        eventRepository.save(event);
    }
}