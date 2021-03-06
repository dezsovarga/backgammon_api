package integration;

import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

/**
 * Created by dezso on 17.12.2017.
 */

public class ApiWrapper {

    TestRestTemplate restTemplate = new TestRestTemplate();
    ObjectMapper mapper = new ObjectMapper();
    HttpHeaders headers = new HttpHeaders();

    private static final String LOGIN_PATH = "/account/login";

    public String registerUser(String path, int port, String jsonBody) throws Exception{
        headers.clear();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<String> registerResponse = callApi(path, port, headers, jsonBody, HttpMethod.POST);
        String decodedResponse = new String(Base64.decodeBase64(registerResponse.getBody().getBytes()));
        return mapper.readValue(decodedResponse, Map.class).get("token").toString();
    }

    public ResponseEntity<String> callApi(String path, int port, HttpHeaders headers, String jsonBody, HttpMethod httpMethod)
    throws IOException{

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = null;

        response = restTemplate.exchange(
                "http://localhost:" + port + path, httpMethod, entity, String.class);

        return response;
    }

    public ResponseEntity<String> confirmUser(String path, int port, String confirmToken) throws Exception {
        headers.clear();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, confirmToken);
        ResponseEntity<String> confirmResponse = callApi(path, port, headers, null, HttpMethod.GET);
        return confirmResponse;
    }

    public String loginUser(int port, String basicAuthToken) throws Exception{
        headers.clear();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, basicAuthToken);
        ResponseEntity<String> authTokenResponse = callApi(LOGIN_PATH, port, headers, null, HttpMethod.POST);
        String decodedResponse = new String(Base64.decodeBase64(authTokenResponse.getBody().getBytes()));
        return mapper.readValue(decodedResponse, Map.class).get("token").toString();

    }
}
