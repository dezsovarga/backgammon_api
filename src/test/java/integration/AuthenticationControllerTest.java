package integration;

import com.dezso.varga.backgammon.BackgammonMain;
import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by dezso on 13.12.2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackgammonMain.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    @LocalServerPort
    private int port;

    ObjectMapper mapper = new ObjectMapper();

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    ApiWrapper apiWrapper = new ApiWrapper();

    private static final String BASIC = "Basic ";

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    String randomEmail = RandomStringUtils.randomAlphabetic(10)+"@varga.com";
    String password = "password";
    Account account = new Account("firstName", "lastNAme", randomEmail, password);

    @Test
    public void testUserLogin() throws Exception {

        RegisterRequest requestBody =new RegisterRequest(account, "verificationLink");
        String jsonBody = mapper.writeValueAsString(requestBody);

        String confirmToken = apiWrapper.registerUser("/account/register", port, jsonBody);
        assertNotNull(confirmToken);

        //User confirmation
        ResponseEntity<String> confirmResponse = apiWrapper.confirmUser("/account/register/confirm", port, confirmToken);
        String decodedConfirmResponse = new String(Base64.decodeBase64(confirmResponse.getBody().getBytes()));
        String authToken = mapper.readValue(decodedConfirmResponse, Map.class).get("token").toString();
        assertNotNull(authToken);

        //Login user
        String basicAuthToken = BASIC + new String(Base64.encodeBase64(String.valueOf(randomEmail+":"+password).getBytes()));
        authToken = apiWrapper.loginUser(port, basicAuthToken);
        assertNotNull(authToken);

        //TODO: add negative tests
        //invalid login
//        randomEmail = randomEmail + "invalidPart";
//        String invalidBasicAuthToken = BASIC + new String(Base64.encodeBase64(String.valueOf(randomEmail+":"+password).getBytes()));
//        authToken = apiWrapper.loginUser(port, invalidBasicAuthToken);
//        assertNotNull(authToken);
    }

    @Test
    public void testGetAllAccounts() throws Exception {

        RegisterRequest requestBody =new RegisterRequest(account, "verificationLink");
        String jsonBody = mapper.writeValueAsString(requestBody);

        String confirmToken = apiWrapper.registerUser("/account/register", port, jsonBody);
        assertNotNull(confirmToken);

        //User confirmation
        ResponseEntity<String> confirmResponse = apiWrapper.confirmUser("/account/register/confirm", port, confirmToken);
        String decodedConfirmResponse = new String(Base64.decodeBase64(confirmResponse.getBody().getBytes()));
        String authToken = mapper.readValue(decodedConfirmResponse, Map.class).get("token").toString();
        assertNotNull(authToken);

        //Login user
        String basicAuthToken = BASIC + new String(Base64.encodeBase64(String.valueOf(randomEmail+":"+password).getBytes()));
        authToken = apiWrapper.loginUser(port, basicAuthToken);
        assertNotNull(authToken);

        //get all accounts
        headers.clear();
        headers.add(HttpHeaders.AUTHORIZATION, authToken);
        ResponseEntity<String> apiResponse = apiWrapper.callApi("/home/users", port, headers, null, HttpMethod.GET);
        List<Account> accounts = mapper.readValue(apiResponse.getBody(), List.class);
        assert(accounts.size()!= 0);

        //get all accounts with invalid token
        headers.clear();
        headers.add(HttpHeaders.AUTHORIZATION, authToken+"invalid");
        apiResponse = apiWrapper.callApi("/home/users", port, headers, null, HttpMethod.GET);
        Map<String, String> responseMap = mapper.readValue(apiResponse.getBody(), Map.class);
        assertEquals("Access Denied", responseMap.get("message"));
        assertEquals(HttpStatus.FORBIDDEN, apiResponse.getStatusCode());
    }

    @Test
    public void testUserConfirmation() throws Exception {

        //User registration

        RegisterRequest requestBody =new RegisterRequest(account, "verificationLink");
        String jsonBody = mapper.writeValueAsString(requestBody);

        String confirmToken = apiWrapper.registerUser("/account/register", port, jsonBody);
        assertNotNull(confirmToken);

        //User confirmation
        ResponseEntity<String> confirmResponse = apiWrapper.confirmUser("/account/register/confirm", port, confirmToken);
        String decodedConfirmResponse = new String(Base64.decodeBase64(confirmResponse.getBody().getBytes()));
        String authToken = mapper.readValue(decodedConfirmResponse, Map.class).get("token").toString();
        assertNotNull(authToken);

        //User already confirmed
        confirmResponse = apiWrapper.confirmUser("/account/register/confirm", port, confirmToken);
        Map<String, String> responseMap = mapper.readValue(confirmResponse.getBody(), Map.class);
        assertEquals("User already verified", responseMap.get("reason"));
        assertEquals(HttpStatus.CONFLICT, confirmResponse.getStatusCode());

        //confirmation token expired or invalid
        confirmResponse = apiWrapper.confirmUser("/account/register/confirm",
                port, confirmToken+"invalid");
        responseMap = mapper.readValue(confirmResponse.getBody(), Map.class);
        assertEquals("Confirmation token expired or invalid", responseMap.get("reason"));
        assertEquals(HttpStatus.PRECONDITION_FAILED, confirmResponse.getStatusCode());
    }
}
