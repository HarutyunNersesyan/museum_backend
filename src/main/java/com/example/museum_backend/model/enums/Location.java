package com.example.museum_backend.model.enums;

public enum Location {
    YEREVAN("Yerevan", "Երևան"),
    GYUMRI("Gyumri", "Գյումրի"),
    VANADZOR("Vanadzor", "Վանաձոր"),
    VAGHARSHAPAT("Vagharshapat (Ejmiatsin)", "Վաղարշապատ (Էջմիածին)"),
    ABOVYAN("Abovyan", "Աբովյան"),
    KAPAN("Kapan", "Կապան"),
    HRAZDAN("Hrazdan", "Հրազդան"),
    ARMAVIR("Armavir", "Արմավիր"),
    ARTASHAT("Artashat", "Արտաշատ"),
    IJEVAN("Ijevan", "Իջևան"),
    GAVAR("Gavar", "Գավառ"),
    GORIS("Goris", "Գորիս"),
    CHARENTSAVAN("Charentsavan", "Չարենցավան"),
    ARARAT("Ararat", "Արարատ"),
    MASIS("Masis", "Մասիս"),
    SEVAN("Sevan", "Սևան"),
    ASHTARAK("Ashtarak", "Աշտարակ"),
    DILIJAN("Dilijan", "Դիլիջան"),
    SISIAN("Sisian", "Սիսիան"),
    ALAVERDI("Alaverdi", "Ալավերդի"),
    STEPANAVAN("Stepanavan", "Ստեփանավան"),
    MARTUNI("Martuni", "Մարտունի"),
    VARDENIS("Vardenis", "Վարդենիս"),
    YEGHVARD("Yeghvard", "Եղվարդ"),
    METSAMOR("Metsamor", "Մեծամոր"),
    BERD("Berd", "Բերդ"),
    TASHIR("Tashir", "Տաշիր"),
    APARAN("Aparan", "Ապարան"),
    VAYK("Vayk", "Վայք"),
    JERMUK("Jermuk", "Ջերմուկ");

    private final String englishName;
    private final String armenianName;

    Location(String englishName, String armenianName) {
        this.englishName = englishName;
        this.armenianName = armenianName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getArmenianName() {
        return armenianName;
    }

    public static Location fromEnglishName(String englishName) {
        for (Location location : values()) {
            if (location.getEnglishName().equals(englishName)) {
                return location;
            }
        }
        return null;
    }

    public static Location fromArmenianName(String armenianName) {
        for (Location location : values()) {
            if (location.getArmenianName().equals(armenianName)) {
                return location;
            }
        }
        return null;
    }
}