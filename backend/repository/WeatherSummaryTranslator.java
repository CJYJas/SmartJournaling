package com.example.smartjournaling.backend.repository;

public class WeatherSummaryTranslator {
    private String malay;

    public WeatherSummaryTranslator(String malay) {
        this.malay = malay;
    }

    private String translateToEnglish(String malay) {

        malay = malay.toLowerCase();

        if (malay.contains("ribut petir"))
            return "Thunderstorms in several places";
        if (malay.contains("tiada hujan"))
            return "No rain";
        if (malay.contains("hujan"))
            return "Rain";
        if (malay.contains("mendung"))
            return "Cloudy";
        if (malay.contains("panas"))
            return "Hot";
        if (malay.contains("kabut"))
            return "Foggy";

        return "Unknown weather";
    }

    public String getEnglishSummary() {
        return translateToEnglish(this.malay);
    }
}
