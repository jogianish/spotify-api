package com.spotify;

import org.json.JSONArray;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
@RestController
@RequestMapping("/get")
public class SpotifyApiApplication {
	public static String clientId = "d6ca024f94c24cef812e82d481ab93c1";
	public static String clientSecret = "d47e5ef9e87e4d6dbc21170078161c51";
	private static String accessTokenUrl = "https://accounts.spotify.com/api/token";
	private static String url = "https://api.spotify.com/v1/playlists/3cEYpjA9oz9GiPac4AsH4n/tracks";

	public static void main(String[] args) {
		SpringApplication.run(SpotifyApiApplication.class, args);

	}

	@GetMapping("/tracks")
	public ResponseEntity<String> getTopTracks() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(clientId, clientSecret);

		String requestBody = "grant_type=client_credentials";

		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, request,
				AccessTokenResponse.class);

		String accessToken = response.getBody().getAccess_token();
		System.out.println("Access token: " + accessToken);

		HttpHeaders headersForGet = new HttpHeaders();
		headersForGet.setBearerAuth(accessToken);

		HttpEntity<String> entity = new HttpEntity<>(headersForGet);
		ResponseEntity<String> responseForGet = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		JSONObject json = new JSONObject(responseForGet.getBody());
		System.out.println(responseForGet.getBody());
		JSONArray items = json.getJSONArray("items");
		System.out.println("Top 10 tracks :");
		StringBuilder output = new StringBuilder();
		output.append("Top 10 Songs :").append("<br>");
		for (int i = 0; i < items.length(); i++) {
			JSONObject track = items.getJSONObject(i).getJSONObject("track");
			System.out.println((i + 1) + ". " + track.getString("name"));
			
			output.append((i + 1)).append(". ").append(track.getString("name")).append("<br>");
		}

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);

		return new ResponseEntity<>(output.toString(), responseHeaders, HttpStatus.OK);

	}
}
