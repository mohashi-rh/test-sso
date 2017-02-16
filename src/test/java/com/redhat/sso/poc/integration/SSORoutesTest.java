package com.redhat.sso.poc.integration;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.HTTPException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.sso.poc.model.Pessoa;
import com.redhat.sso.poc.model.Version;
import com.redhat.sso.poc.ws.CustomerWS;
import com.redhat.sso.poc.ws.VersionWS;

public class SSORoutesTest extends CamelSpringTestSupport {

	private static final String CLIENT_ID = "custom-cxf-endpoint";
	private static final String CLIENT_SECRET = "f74f09ef-2756-4fee-8494-d0a462279345";
	private static final String USERNAME = "user";
	private static final String PASSWORD = "mo123";
	private static final String RH_SSO_URI = "http://localhost:8180/auth/realms/demo/protocol/openid-connect/token";
	private static final String BACKEND_BASE_PATH = "http://localhost:8282";
	private static final String PUBLIC_BACKEND_BASE_PATH = "http://localhost:8383";
	private String expiredToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJkMmUxYjAxNi0zN2I2LTQzZT"
			+ "QtYTQ1My0xZmJmZDE3NTJmYzYiLCJleHAiOjE0ODcyNDcyMDksIm5iZiI6MCwiaWF0IjoxNDg3MjQ2OTA5L"
			+ "CJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvYXV0aC9yZWFsbXMvZGVtbyIsImF1ZCI6ImN1c3RvbS1j"
			+ "eGYtZW5kcG9pbnQiLCJzdWIiOiJjZmE5ZTJkNi1kOTk5LTRkNWUtOTZhYS05MGFlZDI5MjAxYzUiLCJ0eXA"
			+ "iOiJCZWFyZXIiLCJhenAiOiJjdXN0b20tY3hmLWVuZHBvaW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjMxMDc1ND"
			+ "hmLTc2MWYtNDE3OC1iNzliLTE1NjRiMzQ3ZDFjYyIsImNsaWVudF9zZXNzaW9uIjoiZGQyNDJkMmItODkzN"
			+ "i00OTI2LWFmMGEtMTdjNDU3ZWY1ZjZkIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7"
			+ "InJvbGVzIjpbInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6e30sIm5hbWUiOiJVc2VyIFRlc3QiLCJwcmV"
			+ "mZXJyZWRfdXNlcm5hbWUiOiJ1c2VyIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6IlRlc3"
			+ "QiLCJlbWFpbCI6InVzZXJAdGVzdC5jb20ifQ.INjyILHWj7n-A7tGTMqy_Nw-_9qnBeRvJFDj71pILib1u-"
			+ "39yUeBPGNmMtuUr_59BqERdYj831d6XhZ6rPuhi1Qcm-nlVGTStPgvPg1MbP58OvQkgXjX1f02Yd3SI6oZe"
			+ "YRB3WQCzX6uUZTFmW9qzBNEf8xrhin-sowdAOwC2C085s-Jk5DstsrtmrtdTgVyc7Dfa6PWbHDOiBpBfGhv"
			+ "y0yQrnW_Qd25kCFznkS3e2ncTiTBla4H0xhR8ap_7tiVxm9PVh8c0b4XB8F-sCszXl-e7JqF4Jiv4IfWN5H"
			+ "0Umzs9cZA3iH7sd10T-EDhL42Z4M9LN2CyL11RozZV_zo9g";
	private String userAccessToken;
	private String adminAccessToken;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		//Retrieve token
		String rawUserAccessToken = retrieveRawToken("user");

		assertNotNull("access_token can't be null", rawUserAccessToken);
		
		userAccessToken = String.format("Bearer %s", rawUserAccessToken);
		
		String rawAdminAccessToken = retrieveRawToken("admin");
		
		assertNotNull("access_token can't be null", rawAdminAccessToken);
		
		adminAccessToken = String.format("Bearer %s", rawAdminAccessToken);
	}

	private String retrieveRawToken(String scope) {
		return
		/* @formatter:off */
			given().
				contentType(URLENC).
				accept(ANY).
				formParam("client_id", CLIENT_ID).
				formParam("client_secret", CLIENT_SECRET).
				formParam("username", USERNAME).
				formParam("password", PASSWORD).
				formParam("grant_type", "password").
				formParam("scope", scope).
			when().
				post(RH_SSO_URI).
			then().
				statusCode(200).
				contentType(JSON).
				body("access_token", notNullValue()).
			extract().
				jsonPath().
				getString("access_token");
		/* @formatter:on */
	}
	
	@Test
	public void testAccessDeniedToProtectedResources() {
		/* @formatter: off */
			given().
				contentType(JSON).
				accept(JSON).
				body("{ " +
						"\"name\":\"John\"," +
						"\"email\":\"john@company.com\"" +
					 "}").
			when().
				post(BACKEND_BASE_PATH+"/cxf/rest/customer").
			then().
				statusCode(401);
		/* @formatter:on */	
			
		/* @formatter: off */
			given().
				accept(JSON).
			when().
				get(BACKEND_BASE_PATH+"/cxf/rest/customer").
			then().
				statusCode(401);
		/* @formatter:on */
	}
	
	@Test
	public void testAccessDeniedToProtectedWS() {
		try {
			CustomerWS customerWS = createProxy(CustomerWS.class, BACKEND_BASE_PATH+"/cxf/soap/customerWS", null);
			Pessoa pessoa = new Pessoa();
			pessoa.setName("John");
			pessoa.setEmail("john@company.com");
			customerWS.create(pessoa);
			fail("WebServiceException should be thown...");
		} catch (WebServiceException e) {
			assertNotNull("Exception can't be null", e.getCause());
			assertEquals("Exception cause should be of type HTTPException", HTTPException.class, e.getCause().getClass());
			assertTrue("Exception reason should contain [HTTP response '401: Unauthorized']", e.getCause().getMessage().contains("HTTP response '401: Unauthorized'"));
		}

		try {
			CustomerWS customerWS = createProxy(CustomerWS.class, BACKEND_BASE_PATH+"/cxf/soap/customerWS", null);
			customerWS.list();
			fail("WebServiceException should be thown...");
		} catch (WebServiceException e) {
			assertNotNull("Exception can't be null", e.getCause());
			assertEquals("Exception cause should be of type HTTPException", HTTPException.class, e.getCause().getClass());
			assertTrue("Exception reason should contain [HTTP response '401: Unauthorized']", e.getCause().getMessage().contains("HTTP response '401: Unauthorized'"));
		}
	}

	@Test
	public void testUnauthorizedAccessToAdminResource() {
		/* @formatter: off */
			given().
				contentType(JSON).
				accept(JSON).
				header("Authorization", userAccessToken).
				body("{ " +
						"\"name\":\"John\"," +
						"\"email\":\"john@company.com\"" +
					 "}").
			when().
				post(BACKEND_BASE_PATH+"/cxf/rest/customer").
			then().
				statusCode(403);
		/* @formatter:on */	
	}
	
	@Test
	public void testUnauthorizedAccessToAdminWS() {
		try {
			CustomerWS customerWS = createProxy(CustomerWS.class, BACKEND_BASE_PATH+"/cxf/soap/customerWS", new HttpHeaderInterceptor(userAccessToken));
			Pessoa pessoa = new Pessoa();
			pessoa.setName("John");
			pessoa.setEmail("john@company.com");
			customerWS.create(pessoa);
			fail("SOAPFaultException should be thown...");
		} catch (SOAPFaultException e) {
			assertNotNull("Exception can't be null", e.getCause());
			assertEquals("Exception cause should be of type SoapFault", SoapFault.class, e.getCause().getClass());
			assertTrue("Exception reason should contain [Unauthorized]", e.getCause().getMessage().contains("Unauthorized"));
		}
	}
	
	@Test
	public void testExpiredTokenAccessToUserResource() {
		/* @formatter: off */
		given().
			contentType(JSON).
			accept(JSON).
			header("Authorization", expiredToken).
		when().
			get(BACKEND_BASE_PATH+"/cxf/rest/customer").
		then().
			statusCode(401);
		/* @formatter:on */	
	}
	
	@Test
	public void testExpiredTokenAccessToUserWS() {
		try {
			CustomerWS customerWS = createProxy(CustomerWS.class, BACKEND_BASE_PATH+"/cxf/soap/customerWS", new HttpHeaderInterceptor(expiredToken));
			Pessoa pessoa = new Pessoa();
			pessoa.setName("John");
			pessoa.setEmail("john@company.com");
			customerWS.create(pessoa);
			fail("WebServiceException should be thown...");
		} catch (WebServiceException e) {
			e.printStackTrace();
			assertNotNull("Exception can't be null", e.getCause());
			assertEquals("Exception cause should be of type HTTPException", HTTPException.class, e.getCause().getClass());
			assertTrue("Exception reason should contain [HTTP response '401: Unauthorized']", e.getCause().getMessage().contains("HTTP response '401: Unauthorized'"));
		}
	}
	
	@Test
	public void testAllowedAccessToUserResource() {
		/* @formatter: off */
			given().
				accept(JSON).
				header("Authorization", userAccessToken).
			when().
				get(BACKEND_BASE_PATH+"/cxf/rest/customer").
			then().
				statusCode(200).
				contentType(JSON).
				body("size()", is(3));
		/* @formatter:on */	
	}
	
	@Test
	public void testAllowedAccessToAdminResource() {
		/* @formatter: off */
			given().
				contentType(JSON).
				accept(JSON).
				header("Authorization", adminAccessToken).
				body("{ " +
						"\"name\":\"John\"," +
						"\"email\":\"john@company.com\"" +
					 "}").
			when().
				post(BACKEND_BASE_PATH+"/cxf/rest/customer").
			then().
				statusCode(200).
				contentType(JSON).
				content("name", is("John")).
				content("email", is("john@company.com"));
		/* @formatter:on */	
	}
	
	@Test
	public void testPublicResource() {
		/* @formatter: off */
			given().
				accept(JSON).
			when().
				get(PUBLIC_BACKEND_BASE_PATH+"/cxf/rest/version").
			then().
				statusCode(200).
				contentType(JSON).
				content("number", is("1.0"));
		/* @formatter:on */
	}
	
	@Test
	public void testPublicSOAP() {
		VersionWS versionWS = createProxy(VersionWS.class, PUBLIC_BACKEND_BASE_PATH+"/cxf/soap/versionWS", null);
		Version version = versionWS.getVersion();
		
		assertNotNull("Version can't be null!", version);
		assertEquals("Version should be 1.0", "1.0", version.getNumber());
	}

	/**
	 * Creating a new instance of client service
	 * 
	 * @param serviceClass
	 * @param endpoint
	 * @return T proxy
	 */
	@SuppressWarnings("unchecked")
	private <T> T createProxy(Class<T> serviceClass, String endpoint, HttpHeaderInterceptor interceptor) {
		JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
		proxy.setServiceClass(serviceClass);
		proxy.setAddress(endpoint);
		if (interceptor != null) {
			proxy
				.getOutInterceptors().add(interceptor);
		}
		return (T) proxy.create();
	}

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}

	public class HttpHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

		private String token;
		
		public HttpHeaderInterceptor(String token) {
			super(Phase.POST_LOGICAL);
			this.token = token;
		}

		public void handleMessage(Message message) {
			Map<String, List<String>> headers = new HashMap<String, List<String>>();
			headers.put("Authorization", Arrays.asList(token));
			message.put(Message.PROTOCOL_HEADERS, headers);
		}

	}

	public class AdminHttpHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

		public AdminHttpHeaderInterceptor() {
			super(Phase.POST_LOGICAL);
		}

		public void handleMessage(Message message) {
			Map<String, List<String>> headers = new HashMap<String, List<String>>();
			headers.put("Authorization", Arrays.asList(adminAccessToken));
			message.put(Message.PROTOCOL_HEADERS, headers);
		}

	}
}
