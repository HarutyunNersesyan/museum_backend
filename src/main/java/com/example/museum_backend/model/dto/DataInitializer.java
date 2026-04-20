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

        Museum history = new Museum();
        history.setName("History Museum of Armenia");
        history = museumRepository.save(history);

        Event h1 = new Event();
        h1.setName("Հայաստանի պատմություն");
        h1.setDescription("Հայաստանի պատմության թանգարանում տեղի կունենա «Դրվագներ ինքնության. գորգ» խորագրով ցուցադրության փակման արարողությունը։\n" +
                "Միջոցառման ընթացքում կներկայացվեն ցուցադրության վերաբերյալ վիճակագրական տվյալներ, ինչպես նաև թանգարանի գործընկեր «Մեգերյան կարպետ» մշակութային համալիրի հետ համագործակցությամբ ամիսներ շարունակ վերոնշյալ ցուցասրահում գործված գորգի կրկնօրինակը կհամալրի թանգարանի հուշանվերների տեսականին։ Գորգը գործվել է 17-րդ դարով թվագրվող՝ Արցախի մետաքսե ծածկոցի նմանությամբ։ Այս և այլ մանրամասների մասին կխոսի Հայաստանի պատմության թանգարանի Ազգագրության բաժնի գիտաշխատող Լիլիա Ավանեսյանը");
        h1.setEventCategory(EventCategory.HISTORY);
        h1.setEventType(EventType.MOBILE);
        h1.setImageUrls(List.of("http://localhost:8080/uploads/default1.jpg"));
        h1.setEventDate(buildDate(30, 10, 0));
        h1.setPhoneNumber("+374 10 520-690");
        h1.setGuidePrice(1000);
        h1.setTicketPrice(4000);
        h1.setLocation(Location.YEREVAN);
        h1.setDuration(2);
        h1.setMuseum(history);
        eventRepository.save(h1);

        Event h2 = new Event();
        h2.setName("Պատմական Վերադարձ");
        h2.setDescription("Հայաստանի պատմության թանգարանում կբացվի միջնադարյան հայկական փայտարվեստի եզակի ցուցանմուշներից մեկի՝ 1188թ. հայկական եկեղեցու փայտյա դռան փեղկի ցուցադրությունը։\n" +
                "Բացառիկ ցուցանմուշը գնվել է Լոնդոնի «Սեմ Ֆոգ» ցուցասրահից՝ Հայաստանի Հանրապետության կառավարության որոշմամբ և պետական միջոցներով, Մշակույթի զարգացման հիմնադրամի կողմից։ Այն այսուհետ կհամալրի Հայաստանի պատմության թանգարանի հավաքածուն։");
        h2.setEventCategory(EventCategory.HISTORY);
        h2.setEventType(EventType.MOBILE);
        h2.setImageUrls(List.of("http://localhost:8080/uploads/default2.jpg"));
        h2.setEventDate(buildDate(31, 12, 0));
        h2.setPhoneNumber("+374 10 520-690");
        h2.setGuidePrice(1000);
        h2.setTicketPrice(3000);
        h2.setLocation(Location.YEREVAN);
        h2.setDuration(2);
        h2.setMuseum(history);
        eventRepository.save(h2);

        // ==================== MATENADARAN ====================
        Museum matenadaran = new Museum();
        matenadaran.setName("Matenadaran");
        matenadaran = museumRepository.save(matenadaran);

        Event m1 = new Event();
        m1.setName("Ձեռագրերի գաղտնիքները");
        m1.setDescription("Միջնադարյան ձեռագրերի աշխարհ՝ տարբեր լեզուներով բացառիկ նմուշներ։");
        m1.setEventCategory(EventCategory.HISTORY);
        m1.setEventType(EventType.MOBILE);
        m1.setImageUrls(List.of("http://localhost:8080/uploads/default3.webp"));
        m1.setEventDate(buildDate(45, 11, 0));
        m1.setPhoneNumber("+374 10 513-000");
        m1.setGuidePrice(2000);
        m1.setTicketPrice(5000);
        m1.setLocation(Location.YEREVAN);
        m1.setDuration(2);
        m1.setMuseum(matenadaran);
        eventRepository.save(m1);

        Museum erebuni = new Museum();
        erebuni.setName("Erebuni Fortress & Museum");
        erebuni = museumRepository.save(erebuni);

        Event e1 = new Event();
        e1.setName("Էրեբունի պատմություն");
        e1.setDescription("Ուրարտու և Էրեբունի ամրոցի պատմություն։");
        e1.setEventCategory(EventCategory.ARCHAEOLOGY);
        e1.setEventType(EventType.MOBILE);
        e1.setImageUrls(List.of("http://localhost:8080/uploads/default6.jpg"));
        e1.setEventDate(buildDate(60, 13, 0));
        e1.setPhoneNumber("+374 10 461-393");
        e1.setGuidePrice(1000);
        e1.setTicketPrice(2000);
        e1.setLocation(Location.YEREVAN);
        e1.setDuration(2);
        e1.setMuseum(erebuni);
        eventRepository.save(e1);

        // ==================== GENOCIDE ====================
        Museum genocide = new Museum();
        genocide.setName("Armenian Genocide Museum");
        genocide = museumRepository.save(genocide);

        Event g1 = new Event();
        g1.setName("Հիշատակ");
        g1.setDescription("Ցեղասպանության զոհերի հիշատակին նվիրված ցուցադրություն։");
        g1.setEventCategory(EventCategory.HISTORY);
        g1.setEventType(EventType.MOBILE);
        g1.setImageUrls(List.of("http://localhost:8080/uploads/default4.jpg"));
        g1.setEventDate(buildDate(90, 14, 0));
        g1.setPhoneNumber("+374 10 390-980");
        g1.setGuidePrice(1000);
        g1.setTicketPrice(4000);
        g1.setLocation(Location.YEREVAN);
        g1.setDuration(3);
        g1.setMuseum(genocide);
        eventRepository.save(g1);

        Museum cafesjian = new Museum();
        cafesjian.setName("Cafesjian Center for the Arts");
        cafesjian = museumRepository.save(cafesjian);

        // ==================== ADDED MUSEUMS ====================

        Museum dilijan = new Museum();
        dilijan.setName("Dilijan Local Lore Museum");
        dilijan = museumRepository.save(dilijan);

        Museum gyumri = new Museum();
        gyumri.setName("Gyumri Museum of Architecture");
        gyumri = museumRepository.save(gyumri);

        Museum khorVirap = new Museum();
        khorVirap.setName("Khor Virap Museum");
        khorVirap = museumRepository.save(khorVirap);

        Museum parajanov = new Museum();
        parajanov.setName("Sergey Parajanov Museum");
        parajanov = museumRepository.save(parajanov);

        Museum megerian = new Museum();
        megerian.setName("Megerian Carpet Museum");
        megerian = museumRepository.save(megerian);

        Museum louvre = new Museum();
        louvre.setName("Louvre");
        louvre = museumRepository.save(louvre);

        Museum british_museum = new Museum();
        british_museum.setName("British Museum");

        Museum metropolitan = new Museum();
        metropolitan.setName("Metropolitan Museum of Art");
        metropolitan = museumRepository.save(metropolitan);

        // ==================== LOUVRE ====================

        Event l1 = new Event();
        l1.setName("Լուվրի գլուխգործոցներ");
        l1.setDescription("Լուվրի թանգարանում ներկայացվում են համաշխարհային արվեստի ամենահայտնի գործերը՝ սկսած հնագույն քաղաքակրթություններից մինչև Վերածննդի գլուխգործոցներ։ Այցելուները հնարավորություն կունենան տեսնել Մոնա Լիզան և այլ արժեքավոր ստեղծագործություններ։");
        l1.setEventCategory(EventCategory.ART);
        l1.setEventType(EventType.MOBILE);
        l1.setImageUrls(List.of("http://localhost:8080/uploads/default5.jpg"));
        l1.setEventDate(buildDate(120, 11, 0));
        l1.setPhoneNumber("+33 1 40 20 50 50");
        l1.setGuidePrice(5000);
        l1.setTicketPrice(10000);
        l1.setLocation(Location.SEVAN);
        l1.setDuration(3);
        l1.setMuseum(louvre);
        eventRepository.save(l1);


// ==================== BRITISH MUSEUM ====================

        british_museum = museumRepository.save(british_museum);

        Event b1 = new Event();
        b1.setName("Հնագույն քաղաքակրթություններ");
        b1.setDescription("Բրիտանական թանգարանում ներկայացված են հին Եգիպտոսի, Միջագետքի և Հունաստանի եզակի ցուցանմուշներ։ Այցելուները կարող են տեսնել Ռոզետայի քարը և այլ պատմական կարևոր արտեֆակտներ։");
        b1.setEventCategory(EventCategory.HISTORY);
        b1.setEventType(EventType.MOBILE);
        b1.setImageUrls(List.of("http://localhost:8080/uploads/default8.jpg"));
        b1.setEventDate(buildDate(130, 12, 0));
        b1.setPhoneNumber("+44 20 7323 8299");
        b1.setGuidePrice(4000);
        b1.setTicketPrice(9000);
        b1.setLocation(Location.ARTASHAT);
        b1.setDuration(3);
        b1.setMuseum(british_museum);
        eventRepository.save(b1);


// ==================== METROPOLITAN ====================

        Event m2 = new Event();
        m2.setName("Արվեստի գլոբալ ճանապարհորդություն");
        m2.setDescription("Մետրոպոլիտեն արվեստի թանգարանը առաջարկում է արվեստի պատմության լայն շրջանակ՝ ներառյալ եվրոպական, ասիական և ամերիկյան արվեստի գլուխգործոցներ։ Ցուցադրությունը ընդգրկում է հազարավոր տարիների մշակութային զարգացում։");
        m2.setEventCategory(EventCategory.ART);
        m2.setEventType(EventType.MOBILE);
        m2.setImageUrls(List.of("http://localhost:8080/uploads/default7.jpg"));
        m2.setEventDate(buildDate(140, 13, 0));
        m2.setPhoneNumber("+1 212-535-7710");
        m2.setGuidePrice(6000);
        m2.setTicketPrice(12000);
        m2.setLocation(Location.IJEVAN);
        m2.setDuration(4);
        m2.setMuseum(metropolitan);
        eventRepository.save(m2);
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