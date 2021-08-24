package egovframework.example.sample.board.web;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import egovframework.rte.fdl.property.EgovPropertyService;

public class NaverLoginBO {
	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	/* 인증 요청문을 구성하는 파라미터 */
	// client_id: 애플리케이션 등록 후 발급받은 아이디
	// response_type: 인증 과정에 대한 구분값. code 값

	private final static boolean DEBUG = true;

	private final static String SESSION_STATE = "oauth_state";
	/* 프로필 조회 API URL */
	private final static String PROFILE_API_URL = "https://openapi.naver.com/v1/nid/me";

	/* 네이버 아이디로 인증 URL 생성 Method */
	public String getAuthorizationUrl(HttpSession session, String returnUrlGubun) {
		String clientId = propertiesService.getString("naver.api.clientId");
		String clientSecret = propertiesService.getString("naver.api.clientSecret");
		String redirectUri = propertiesService.getString("naver.api.redirectUri") + returnUrlGubun + ".do";
		String siteUrl = propertiesService.getString("site.url");

		/* 세션 유효성 검증을 위하여 난수를 생성 */
		String state = generateRandomString();
		/* 생성한 난수 값을 session에 저장 */

		if (DEBUG) {
			System.out.println("naver getAuthorizationUrl");
			System.out.println("state [" + state + "]");

		}

		setSession(session, state);

		System.out.println(clientId);
		System.out.println(clientSecret);
		System.out.println(siteUrl + redirectUri);

		/* Scribe에서 제공하는 인증 URL 생성 기능을 이용하여 네아로 인증 URL 생성 */
		OAuth20Service oauthService = new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret)
				.callback(siteUrl + redirectUri).state(state) // 앞서 생성한 난수값을 인증 URL생성시 사용함
				.build(NaverLoginApi.instance());
		return oauthService.getAuthorizationUrl();
	}

	/* 네이버아이디로 Callback 처리 및 AccessToken 획득 Method */
	public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state, String returnUrlGubun)
			throws IOException {
		String clientId = propertiesService.getString("naver.api.clientId");
		String clientSecret = propertiesService.getString("naver.api.clientSecret");
		String redirectUri = propertiesService.getString("naver.api.redirectUri") + returnUrlGubun + ".do";
		String siteUrl = propertiesService.getString("site.url");

		if (DEBUG) {
			System.out.println("naver 인증==================================");
			System.out.println("clientId [" + clientId + "]");
			System.out.println("clientSecret [" + clientSecret + "]");
			System.out.println("redirectUri [" + redirectUri + "]");
			System.out.println("siteUrl [" + siteUrl + "]");
		}
		/* Callback으로 전달받은 세선검증용 난수값과 세션에 저장되어있는 값이 일치하는지 확인 */
		String sessionState = getSession(session);
		System.out.println("sessionState [" + sessionState + "]");
		System.out.println("state [" + state + "]");

		if (StringUtils.pathEquals(sessionState, state)) {
			OAuth20Service oauthService = new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret)
					.callback(siteUrl + redirectUri).state(state).build(NaverLoginApi.instance());
			/* Scribe에서 제공하는 AccessToken 획득 기능으로 네아로 Access Token을 획득 */
			OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
			return accessToken;
		}
		return null;
	}

	/* 세션 유효성 검증을 위한 난수 생성기 */
	private String generateRandomString() {
		return UUID.randomUUID().toString();
	}

	/* http session에 데이터 저장 */
	private void setSession(HttpSession session, String state) {
		session.setAttribute(SESSION_STATE, state);
	}

	/* http session에서 데이터 가져오기 */
	private String getSession(HttpSession session) {
		return (String) session.getAttribute(SESSION_STATE);
	}

	/* Access Token을 이용하여 네이버 사용자 프로필 API를 호출 */
	public String getUserProfile(OAuth2AccessToken oauthToken, String returnUrlGubun) throws IOException {
		String clientId = propertiesService.getString("naver.api.clientId");
		String clientSecret = propertiesService.getString("naver.api.clientSecret");
		String redirectUri = propertiesService.getString("naver.api.redirectUri") + returnUrlGubun + ".do";
		String siteUrl = propertiesService.getString("site.url");

		OAuth20Service oauthService = new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret)
				.callback(siteUrl + redirectUri).build(NaverLoginApi.instance());
		OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_API_URL, oauthService);
		oauthService.signRequest(oauthToken, request);
		Response response = request.send();
		return response.getBody();
	}
}
