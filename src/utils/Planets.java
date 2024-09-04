package utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

/**
 * author: ahror
 * <p>
 * since: 9/1/24
 */
public final class Planets {
    public static final Map<String, Float> planetGravity = new TreeMap<>() {{
        put("Mercury", 0.38f);
        put("Venus", 0.91f);
        put("Earth", 1.0f);
        put("Moon", 0.166f);
        put("Mars", 0.38f);
        put("Jupiter", 2.34f);
        put("Saturn", 0.93f);
        put("Uranus", 0.89f);
        put("Neptune", 1.12f);
        put("Pluto", 0.06f);
        put("Sun", 27.09f);
    }};
    @Getter
    private static String currentPlanet = "Earth";

    public static float currentGravity = planetGravity.get(currentPlanet);

    public static void setCurrentPlanet(String selectedPlanet) {
        currentPlanet = selectedPlanet;
        currentGravity = planetGravity.get(currentPlanet);
    }
}
