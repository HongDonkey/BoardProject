package egovframework.example.sample.board.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//import egovframework.com.cmm.service.EgovFileMngUtil;
//import egovframework.com.cmm.service.Globals;
import egovframework.example.sample.board.service.BoardService;
import egovframework.example.sample.board.service.BoardVO;
import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import com.github.scribejava.core.model.OAuth2AccessToken;

@Controller
public class BoardController {

	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@Resource(name = "boardService")
	private BoardService boardService;

	private final static String STR_USER = "User";

	@Autowired
	private NaverLoginBO naverLoginBO;

	private String apiResult = null;

	@Autowired
	private KakaoLoginApi kakao;

	@Autowired
	private FacebookConnectionFactory connectionFactory;

	@Autowired
	private OAuth2Parameters oAuth2Parameters;

	@RequestMapping(value = "/mainList.do")
	public String list(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {
		/** EgovPropertyService.sample */
		/** resource -> egov -> spring -> properties.xml **/
		boardVO.setPageUnit(propertiesService.getInt("pageUnit"));
		boardVO.setPageSize(propertiesService.getInt("pageSize"));
		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(boardVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(boardVO.getPageUnit());
		paginationInfo.setPageSize(boardVO.getPageSize());
		boardVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		boardVO.setLastIndex(paginationInfo.getLastRecordIndex());
		boardVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		List<?> list = boardService.selectBoardList(boardVO);
		// resultlist??? list??? ??????
		model.addAttribute("resultList", list);
		int totCnt = boardService.selectBoardListTotCnt(boardVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);
		return "/board/mainList";
	}

	@RequestMapping(value = "/mgmt.do", method = RequestMethod.GET)
	public String mgmg(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model, HttpServletRequest request)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c1 = Calendar.getInstance();
		String strToday = sdf.format(c1.getTime());
		System.out.println("Today" + strToday);

		// ???????????? ????????????
		boardVO = boardService.selectBoard(boardVO);
		// BoardVO boardVO = new BoardVO();
		boardVO.setIndate(strToday);
		boardVO.setWriter(request.getSession().getAttribute("userId").toString());
		boardVO.setWriterNm(request.getSession().getAttribute("userName").toString());

		// ???????????? ????????????
//		if (request.getAttribute("idx") != null) {
//			boardVO = boardService.selectBoard(boardVO);
//			model.addAttribute("boardVO", boardVO);
//		}
		// ???????????? ??????????????? ????????? ??????
		model.addAttribute("boardVO", boardVO);

		return "/board/mgmt";
	}

	@RequestMapping(value = "/mgmt.do", method = RequestMethod.POST)
	public String mgmg2(@ModelAttribute("boardVO") BoardVO boardVO, @RequestParam("mode") String mode, ModelMap model)
			throws Exception {

//		boardService.insertBoard(boardVO);
		if ("add".equals(mode)) {
			boardService.insertBoard(boardVO);
		} else if ("modify".equals(mode)) {
			boardService.updateBoard(boardVO);
		} else if ("del".equals(mode)) {
			boardService.deleteBoard(boardVO);
		}

		return "redirect:/mainList.do";
	}

	@RequestMapping(value = "/view.do")
	public String view(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c1 = Calendar.getInstance();
		String strToday = sdf.format(c1.getTime());
		System.out.println("Today" + strToday);
		boardService.updateBoardCount(boardVO);

		boardVO = boardService.selectBoard(boardVO);
		model.addAttribute("boardVO", boardVO);
		model.addAttribute("strToday", strToday);

		List<?> list = boardService.selectReplyList(boardVO);
		model.addAttribute("resultList", list);

		return "/board/view";
	}

	@RequestMapping(value = "/reply.do", method = RequestMethod.POST)
	public String reply(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {

		boardService.insertReply(boardVO);

		return "redirect:/view.do?idx=" + boardVO.getIdx();
	}

	@RequestMapping(value = "/login.do")
	public String login(@RequestParam("user_id") String user_id, @RequestParam("password") String password,
			HttpServletRequest request, HttpSession session, ModelMap model) throws Exception {
		String returnUrlGubun = STR_USER;
		// HttpServletRequest request
		// String aa = request.getParameter("user_id");

		// OAuth??? ?????? ?????? ???????????? ??????
		if (!"".equals(user_id) && !"".equals(password)) {

			BoardVO boardVO = new BoardVO();
			boardVO.setUserId(user_id);
			boardVO.setPassword(password);
			String user_name = boardService.selectLoginCheck(boardVO);

			if (user_name != null && !"".equals(user_name)) {
				request.getSession().setAttribute("userId", user_id);
				request.getSession().setAttribute("userName", user_name);
			} else {
				request.getSession().setAttribute("userId", "");
				request.getSession().setAttribute("userName", "");
				model.addAttribute("msg", "????????? ????????? ???????????? ????????????.");
			}
			return "redirect:mainList.do";

			// OAuth ????????? ????????? ??? login.jsp ?????? ??????
		} else {
			// ?????????
			String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session, returnUrlGubun);
			model.addAttribute("naver_url", naverAuthUrl);
			// ????????????
			OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
			oAuth2Parameters.setRedirectUri(propertiesService.getString("site.secure.url")
					+ propertiesService.getString("facebook.api.redirectUri") + returnUrlGubun + ".do");
			String facebook_url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, oAuth2Parameters);
			model.addAttribute("facebook_url", facebook_url);
			model.addAttribute("kakao_url", kakao.getKakaoUrl(returnUrlGubun));
			return "/board/login";
		}

	}

	// ???????????? ?????????
	@RequestMapping(value = "/kakaoCallbackUser.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String kakaoCallbackSports(ModelMap model, @RequestParam("code") String code, HttpSession session,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		return kakaoCallback(model, code, session, response, request);
	}

	// ???????????? ?????????
	public String kakaoCallback(ModelMap model, String code, HttpSession session, HttpServletResponse response,
			HttpServletRequest request) throws Exception {
		String access_Token = kakao.getAccessToken(code, STR_USER);
		HashMap<String, Object> userInfo = kakao.getUserInfo(access_Token);
		HashMap<String, Object> map = new HashMap<>();
		String id = (String) userInfo.get("id");
		System.out.println("id " + id);

		// [TODO] DB ???????????? ?????? ????????? ???????????? ?????? ??????
//	        ?????? ????????? ????????? ?????? ??? ?????? ?????? ??????
//	        ????????? ???????????? ?????? ?????? ?????????????????? ??????.

		testLogin(id, session, model);

		return "redirect:mainList.do";
	}

	// ????????? ????????? ??? ?????? ???????????? ??????
	@RequestMapping(value = "/naverCallbackUser.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String naverCallbackUser(ModelMap model, @RequestParam String code, @RequestParam String state,
			HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {
		String cmpnyNm = STR_USER;
		return naverCallback(model, code, state, cmpnyNm, session, response, request);
	}

	private String naverCallback(ModelMap model, String code, String state, String cmpnyNm, HttpSession session,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		OAuth2AccessToken oauthToken;
		oauthToken = naverLoginBO.getAccessToken(session, code, state, cmpnyNm);
		apiResult = naverLoginBO.getUserProfile(oauthToken, cmpnyNm); // String????????? json?????????
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(apiResult);
		JSONObject jsonObj = (JSONObject) obj;
		String resultcode = (String) jsonObj.get("resultcode");

		JSONObject response_obj = (JSONObject) jsonObj.get("response");
		String id = (String) response_obj.get("id");
		if (!resultcode.equals("00")) {
			System.out.println("????????? ????????? ?????????????????????. ?????? ???????????? ????????????.");
		}

		// [TODO] DB ???????????? ?????? ????????? ???????????? ?????? ??????
//	        ?????? ????????? ????????? ?????? ??? ?????? ?????? ??????
//	        ????????? ???????????? ?????? ?????? ?????????????????? ??????.
		testLogin(id, session, model);
		return "redirect:mainList.do";
	}

	@RequestMapping(value = "/facebookCallbackUser.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String facebookCallbackUser(@RequestParam String code, HttpServletRequest request, HttpSession session,
			HttpServletResponse response, ModelMap model) throws Exception {
		String cmpnyNm = STR_USER;
		return facebookCallback(code, cmpnyNm, request, session, response, model);

	}

	// ???????????? ????????? ??? ?????? ???????????? ??????
	public String facebookCallback(String code, String cmpnyNm, HttpServletRequest request, HttpSession session,
			HttpServletResponse response, ModelMap model) throws Exception {
		try {
			String redirectUri = propertiesService.getString("site.secure.url")
					+ propertiesService.getString("facebook.api.redirectUri") + cmpnyNm + ".do";

			OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
			AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, redirectUri, null);
			String accessToken = accessGrant.getAccessToken();
			System.out.println("AccessToken: " + accessToken);
			Long expireTime = accessGrant.getExpireTime();

			if (expireTime != null && expireTime < System.currentTimeMillis()) {
				accessToken = accessGrant.getRefreshToken();
				System.out.println("accessToken is expired. refresh token = {}" + accessToken);
			}
			;

			Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);
			Facebook facebook = connection == null ? new FacebookTemplate(accessToken) : connection.getApi();
			facebook.userOperations();

			try {
				String[] fields = { "id", "email", "name" };
				User userProfile = facebook.fetchObject("me", User.class, fields);
				System.out.println("?????? id : " + userProfile.getId());
				String id = userProfile.getId();
				System.out.println("Facebook id : " + id);

				// [TODO] DB ???????????? ?????? ????????? ???????????? ?????? ?????? //?????? ????????? ????????? ?????? ??? ?????? ?????? ?????? // ????????? ???????????? ?????? ??????
				// ?????????????????? ??????.
				testLogin(id, session, model);
			} catch (MissingAuthorizationException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:mainList.do";
	}

	@RequestMapping(value = "/logout.do")
	public String logout(ModelMap model, HttpServletRequest request) throws Exception {
		request.getSession().invalidate();
		return "redirect:mainList.do";
	}

	public String selectLoginCheck(BoardVO searchVO) {
		return boardService.selectLoginCheck(searchVO);
	}

	public String testLogin(String user_id, HttpSession session, ModelMap model) {
		System.out.println("userid:" + user_id);

		session.setAttribute("userId", "guest");
		session.setAttribute("userName", "?????????");
		return "redirect:mainList.do";
	}

}
