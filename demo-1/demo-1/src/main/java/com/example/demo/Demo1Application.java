package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Demo1Application {

	public static void main(String[] args) {
		 if (args.length != 2) {
	            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <Path to JSON File>");
	            return;
	        }

	        String prnNumber = args[0].toLowerCase().trim();
	        String jsonFilePath = args[1];

	        try {
	            // Step 1: Parse JSON file and find the first instance of the key "destination"
	            String destinationValue = findFirstDestinationValue(jsonFilePath);

	            if (destinationValue == null) {
	                System.out.println("Key 'destination' not found in the JSON file.");
	                return;
	            }

	            // Step 2: Generate a random alphanumeric string of size 8 characters
	            String randomString = generateRandomString(8);

	            // Step 3: Concatenate PRN number, destination value, and random string
	            String concatenatedString = prnNumber + destinationValue + randomString;

	            // Step 4: Generate MD5 hash of the concatenated string
	            String md5Hash = generateMD5Hash(concatenatedString);

	            // Step 5: Output the result in the specified format
	            System.out.println(md5Hash + ";" + randomString);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    private static String findFirstDestinationValue(String jsonFilePath) throws IOException {
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
	        return traverseJson(rootNode);
	    }

	    private static String traverseJson(JsonNode node) {
	        if (node.isObject()) {
	            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
	            while (fields.hasNext()) {
	                Map.Entry<String, JsonNode> field = fields.next();
	                if ("destination".equals(field.getKey())) {
	                    return field.getValue().asText();
	                }
	                String value = traverseJson(field.getValue());
	                if (value != null) return value;
	            }
	        } else if (node.isArray()) {
	            for (JsonNode item : node) {
	                String value = traverseJson(item);
	                if (value != null) return value;
	            }
	        }
	        return null;
	    }

	    private static String generateRandomString(int length) {
	        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	        Random random = new Random();
	        StringBuilder sb = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {
	            sb.append(characters.charAt(random.nextInt(characters.length())));
	        }
	        return sb.toString();
	    }

	    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] messageDigest = md.digest(input.getBytes());
	        StringBuilder sb = new StringBuilder();
	        for (byte b : messageDigest) {
	            sb.append(String.format("%02x", b));
	        }
	        return sb.toString();
	}

}
