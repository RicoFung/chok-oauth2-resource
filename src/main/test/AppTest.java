import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.Application;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AppTest
{
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

//	@Test
	public void todo()
	{
		System.out.println(passwordEncoder.encode("secret"));
	}

	@Test
	// 测试时需MARK掉 AuthServerConfig类的oauthServer.allowFormAuthenticationForClients();
	public void givenDBUser_whenRevokeToken_thenAuthorized()
	{
		String accessToken = obtainAccessToken("client1", "rico", "123");
		System.out.println(accessToken);
//		assertNotNull(accessToken);
	}

	private String obtainAccessToken(String clientId, String username, String password)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "password");
		params.put("client_id", clientId);
		params.put("username", username);
		params.put("password", password);
		Response response = RestAssured.given().auth().preemptive().basic(clientId, "secret").and().with()
				.params(params).when().post("http://localhost:9099/oauth/token");
		System.out.println(response.asString());
		return response.jsonPath().getString("access_token");
	}
}
