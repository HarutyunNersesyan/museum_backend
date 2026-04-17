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

        // ==================== HISTORY ====================
        Museum history = saveMuseum("History Museum of Armenia");

        createEvent(history,
                "Հնագույն քաղաքակրթություններ",
                "Այս ցուցադրությունը ներկայացնում է Հայաստանի տարածքում ձևավորված առաջին քաղաքակրթությունները՝ սկսած նախնադարից մինչև հին թագավորությունների ձևավորում։ Այցելուները կարող են տեսնել հնագիտական եզակի գտածոներ, կենցաղային իրեր և մշակութային արժեքներ։",
                EventCategory.HISTORY, Location.YEREVAN, 30, 10);

        createEvent(history,
                "Հայկական թագավորություններ",
                "Ցուցադրությունը նվիրված է հայկական թագավորությունների պատմությանը՝ ներկայացնելով նրանց զարգացումը, ռազմական ուժը և մշակութային ժառանգությունը։ Ներկայացված են քարտեզներ, զենքեր և պատմական փաստաթղթեր։",
                EventCategory.HISTORY, Location.YEREVAN, 31, 12);

        // ==================== MATENADARAN ====================
        Museum matenadaran = saveMuseum("Matenadaran");

        createEvent(matenadaran,
                "Ձեռագրերի գաղտնիքները",
                "Այս միջոցառումը ներկայացնում է միջնադարյան ձեռագրերի հարուստ աշխարհը՝ ընդգրկելով տարբեր լեզուներով գրված բացառիկ նմուշներ։ Այցելուները կարող են բացահայտել գրերի զարգացումը և մշակութային արժեքը։",
                EventCategory.HISTORY, Location.YEREVAN, 45, 11);

        createEvent(matenadaran,
                "Հին գրերի զարգացում",
                "Ցուցադրությունը ներկայացնում է գրերի պատմական զարգացումը՝ սկսած հին ձեռագրերից մինչև միջնադարյան գրականություն։ Ներկայացված են եզակի նմուշներ և պատմական փաստաթղթեր։",
                EventCategory.HISTORY, Location.YEREVAN, 46, 13);

        // ==================== CAFESJIAN ====================
        Museum cafesjian = saveMuseum("Cafesjian Center for the Arts");

        createEvent(cafesjian,
                "Ժամանակակից արվեստ",
                "Ցուցահանդեսը ներկայացնում է ժամանակակից արվեստի բազմազան ուղղություններ՝ ներառյալ աբստրակտ և փորձարարական գործեր։ Այցելուները հնարավորություն կունենան բացահայտել նորարարական մոտեցումներ արվեստում։",
                EventCategory.ART, Location.YEREVAN, 20, 12);

        createEvent(cafesjian,
                "Քանդակների ցուցադրություն",
                "Այս ցուցադրությունը ներկայացնում է հայկական և միջազգային քանդակագործների աշխատանքները՝ ընդգծելով ժամանակակից արվեստի նոր ձևերը և արտահայտչամիջոցները։",
                EventCategory.ART, Location.YEREVAN, 21, 14);

        // ==================== EREBUNI ====================
        Museum erebuni = saveMuseum("Erebuni Fortress & Museum");

        createEvent(erebuni,
                "Էրեբունի պատմություն",
                "Ցուցադրությունը նվիրված է Էրեբունի ամրոցի հիմնադրմանը և ուրարտական մշակույթին։ Այստեղ ներկայացված են հնագիտական գտածոներ, որմնանկարներ և պատմական փաստեր։",
                EventCategory.ARCHAEOLOGY, Location.YEREVAN, 60, 13);

        createEvent(erebuni,
                "Ուրարտու մշակույթ",
                "Այս միջոցառումը ներկայացնում է Ուրարտու թագավորության մշակույթը, տնտեսությունը և ռազմական համակարգը՝ ցուցադրելով եզակի հնագիտական նմուշներ։",
                EventCategory.ARCHAEOLOGY, Location.YEREVAN, 61, 15);

        // ==================== GENOCIDE ====================
        Museum genocide = saveMuseum("Armenian Genocide Museum");

        createEvent(genocide,
                "Հիշատակ",
                "Ցուցադրությունը նվիրված է Հայոց ցեղասպանության զոհերի հիշատակին՝ ներկայացնելով պատմական փաստեր, վավերագրեր և անձնական պատմություններ։",
                EventCategory.HISTORY, Location.YEREVAN, 90, 14);

        createEvent(genocide,
                "Վերածնունդ",
                "Այս միջոցառումը ներկայացնում է հայ ժողովրդի վերածնունդը ցեղասպանությունից հետո՝ ընդգծելով մշակութային և ազգային ինքնության պահպանման կարևորությունը։",
                EventCategory.HISTORY, Location.YEREVAN, 91, 16);

        // ==================== DILIJAN ====================
        Museum dilijan = saveMuseum("Dilijan Local Lore Museum");

        createEvent(dilijan,
                "Բնություն",
                "Ցուցադրությունը ներկայացնում է Դիլիջանի բնական միջավայրը, բուսական և կենդանական աշխարհը՝ ընդգծելով էկոհամակարգերի կարևորությունը։",
                EventCategory.NATURAL_HISTORY, Location.DILIJAN, 40, 15);

        createEvent(dilijan,
                "Մշակույթ",
                "Այս միջոցառումը ներկայացնում է Դիլիջանի մշակութային ժառանգությունը, ավանդույթներն ու տեղական արհեստները։",
                EventCategory.NATURAL_HISTORY, Location.DILIJAN, 41, 17);

        // ==================== MEGERIAN ====================
        Museum megerian = saveMuseum("Megerian Carpet Museum");

        createEvent(megerian,
                "Գորգերի արվեստ",
                "Ցուցադրությունը ներկայացնում է հայկական գորգագործության պատմությունն ու զարգացումը՝ ցուցադրելով տարբեր ժամանակաշրջանների նմուշներ։",
                EventCategory.CULTURAL, Location.YEREVAN, 55, 16);

        createEvent(megerian,
                "Վարպետության դաս",
                "Այցելուները կարող են մասնակցել գորգագործության վարպետության դասերի՝ ծանոթանալով ավանդական տեխնիկաներին և նյութերին։",
                EventCategory.CULTURAL, Location.YEREVAN, 56, 18);

        // ==================== PARAJANOV ====================
        Museum parajanov = saveMuseum("Sergey Parajanov Museum");

        createEvent(parajanov,
                "Կոլաժներ",
                "Ցուցադրությունը ներկայացնում է Սերգեյ Փարաջանովի յուրահատուկ կոլաժները՝ արտահայտելով նրա ստեղծագործական աշխարհը։",
                EventCategory.ART, Location.YEREVAN, 25, 17);

        createEvent(parajanov,
                "Արվեստի աշխարհ",
                "Այս միջոցառումը բացահայտում է Փարաջանովի արվեստի բազմաշերտ աշխարհը՝ ներառելով գծանկարներ և անձնական իրեր։",
                EventCategory.ART, Location.YEREVAN, 26, 19);

        // ==================== KHOR VIRAP ====================
        Museum khorVirap = saveMuseum("Khor Virap Museum");

        createEvent(khorVirap,
                "Քրիստոնեություն",
                "Ցուցադրությունը ներկայացնում է Հայաստանում քրիստոնեության ընդունման պատմությունը և դրա նշանակությունը ազգային ինքնության համար։",
                EventCategory.RELIGIOUS, Location.ARARAT, 70, 18);

        createEvent(khorVirap,
                "Սրբավայր պատմություն",
                "Այս միջոցառումը ներկայացնում է Խոր Վիրապի պատմությունը՝ որպես կարևոր հոգևոր կենտրոն և ուխտատեղի։",
                EventCategory.RELIGIOUS, Location.ARARAT, 71, 19);

        // ==================== GYUMRI ====================
        Museum gyumri = saveMuseum("Gyumri Museum of Architecture");

        createEvent(gyumri,
                "Ճարտարապետություն",
                "Ցուցադրությունը ներկայացնում է Գյումրու ճարտարապետական ժառանգությունը՝ ընդգծելով 19-րդ դարի շենքերը և քաղաքային միջավայրը։",
                EventCategory.HISTORY, Location.GYUMRI, 35, 19);

        createEvent(gyumri,
                "Հին շենքեր",
                "Այս միջոցառումը ներկայացնում է Գյումրու հին շենքերի պատմությունը և դրանց պահպանման կարևորությունը։",
                EventCategory.HISTORY, Location.GYUMRI, 36, 20);
    }

    private Museum saveMuseum(String name) {
        Museum museum = new Museum();
        museum.setName(name);
        return museumRepository.save(museum);
    }

    private void createEvent(Museum museum, String name, String description,
                             EventCategory category, Location location,
                             int days, int hour) {

        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setEventCategory(category);
        event.setEventType(EventType.MOBILE);
        event.setImageUrls(List.of("http://localhost:8080/uploads/default.jpg"));
        event.setEventDate(buildDate(days, hour, 0));
        event.setPhoneNumber("+37400000000");
        event.setContactEmail("info@museum.am");
        event.setGuidePrice(1000);
        event.setTicketPrice(2000);
        event.setLocation(location);
        event.setDuration(2);
        event.setMuseum(museum);

        eventRepository.save(event);
    }

    private LocalDateTime buildDate(int days, int hour, int minute) {
        return LocalDateTime.now()
                .plusDays(days)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
    }
}