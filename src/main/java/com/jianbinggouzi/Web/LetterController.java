package com.jianbinggouzi.Web;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jianbinggouzi.Domain.Letter;
import com.jianbinggouzi.Domain.User;
import com.jianbinggouzi.Exception.ExceptionWithMessage;
import com.jianbinggouzi.Service.TextEntityService;

@Controller
@RequestMapping("/letter")
public class LetterController extends BaseController {

	private TextEntityService textEntityService;

	@Autowired
	public void seLetterService(TextEntityService textEntityService) {
		this.textEntityService = textEntityService;
	}

	/**
	 * 添加信件
	 * 
	 * @param request
	 * @param letterTitle
	 * @param text
	 * @return 信件实体的主键
	 * @throws Exception
	 */
	@RequestMapping("/add")
	@ResponseBody
	public HashMap<String, String> addLetter(String token, String letterTitle, String text) throws Exception {

		User user = getUser(token);
		String key = textEntityService.addLetterOrDynamics(user, letterTitle, text);
		System.out.println(key);
		HashMap<String, String> res = new HashMap<>();
		res.put("result", key);
		return res;
	}

	/**
	 * 修改信件实体
	 * 
	 * @param request
	 * @param letterTitle
	 * @param text
	 * @param id
	 * @return 修改记录实体的主键
	 * @throws ExceptionWithMessage
	 */
	@RequestMapping("/update")
	@ResponseBody
	public String updateLetter(String token, String letterTitle, String text, String id) throws ExceptionWithMessage {
		User user = getUser(token);
		return textEntityService.updateLetterOrDynamics(user, letterTitle, text, id);
	}

	/**
	 * 分页获取user的所有Letter
	 * 
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ExceptionWithMessage
	 */
	@RequestMapping("/allfromuser")
	@ResponseBody
	public List<Letter> getAllLetterOfUser(String token, int pageNo, int pageSize) throws ExceptionWithMessage {
		User user = getUser(token);
		List<Letter> list = (List<Letter>) (Object) (textEntityService.getAllTextFromUserByTime(user, "letter", pageNo,
				pageSize));

		return list;
	}

	/**
	 * 分页获取所有Letter 用于首页
	 * 
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ExceptionWithMessage
	 */
	@RequestMapping("/all")
	@ResponseBody
	public List<Letter> getAllLetter(HttpServletRequest request, HttpServletResponse reponse, Integer pageNo,
			Integer pageSize) throws ExceptionWithMessage {
		List<Letter> list = (List<Letter>) (Object) (textEntityService.getAllLetterOrDynamics("letter", pageNo,
				pageSize));
		return list;
	}

	/**
	 * 分页获取所有关注者的Letter
	 * 
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/allLetterOfFollower")
	@ResponseBody
	public List<Letter> getAllLetterFromFllowers(String token, Integer pageNo, Integer pageSize) {
		return this.textEntityService.getTextEntityFromFollowers(getUser(token), "letter", pageNo, pageSize);
	}

	/**
	 * 根据id获取Letter实例
	 * 
	 * @param entityId
	 * @return
	 */
	@RequestMapping("/getLetter")
	@ResponseBody
	public Letter getLetter(String entityId) {
		return (Letter) this.textEntityService.getTextEntityById(entityId, "letter");
	}

}
