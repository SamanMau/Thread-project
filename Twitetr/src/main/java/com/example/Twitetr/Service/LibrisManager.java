package com.example.Twitetr.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.HashMap;

@Service
public class LibrisManager {
    private static final String LIBRIS_API_URL = "http://api.libris.kb.se/bibspell/spell?query=%s&key=%s";

    public LibrisManager(){

    }

    public HashMap<String, Object> checkSpelling(String userInput, String specified_language) {
        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, Object> map = new HashMap<>();

        if(specified_language.isEmpty()){
            map.put("invalid", "Language is missing.");
            return map;
        }

        if (userInput.isEmpty()) {
            map.put("invalid", "Text is missing.");
            return map;
        }

        try {
            String key = System.getenv("LIBRIS_API_NYCKEL");
            System.out.println("laddad nyckel: " + key);

            if (key == null || key.isEmpty()) {
                map.put("invalid", "The API key is missing");
                return map;
            }



            String URL = String.format(LIBRIS_API_URL, URLEncoder.encode(userInput, 
            StandardCharsets.UTF_8), key);

            //skickar GET förfrågan till LIBRIS
            ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);


            var result = response.getBody();

            if (result == null || result.isEmpty()) {
                map.put("invalid", "LIBRIS API returned an empty answer.");
                return map;
            }

            if(response.getStatusCode() != HttpStatus.OK){
                map.put("invalid", "Fel vid API-anrop: " + response.getStatusCode());
                return map;
            }

            ObjectMapper oM = new ObjectMapper();

            return oM.readValue(response.getBody(), HashMap.class);
          
        } catch (Exception e) {
            e.printStackTrace();
            map.put("invalid", "Fel uppstod vid kommunikation med LIBRIS. ");
            return map;
        }
    }

    public boolean verifyApiKey(){
        String key = System.getenv("LIBRIS_API_NYCKEL");

        if(key != null){
            return true;
        }

        if(!key.isEmpty()){
            return true;
        }

        return false;
    }
}