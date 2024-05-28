package jp.co.internous.team2403.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.team2403.model.domain.TblCart;
import jp.co.internous.team2403.model.domain.dto.CartDto;
import jp.co.internous.team2403.model.form.CartForm;
import jp.co.internous.team2403.model.mapper.TblCartMapper;
import jp.co.internous.team2403.model.session.LoginSession;


/**
 * カート情報に関する処理のコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2403/cart")
public class CartController {	

	@Autowired
	private TblCartMapper cartMapper;
	@Autowired
	private LoginSession loginSession;

	private Gson gson = new Gson();
	
	/**
	 * カート画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		int tempUserId = loginSession.getTmpUserId();
		int userId = loginSession.getUserId();
		if(userId != 0) {
			cartMapper.updateUserId(userId, tempUserId);
			List<CartDto> carts = cartMapper.findByUserId(loginSession.getUserId());
			m.addAttribute("carts", carts);
		} else {
			List<CartDto> carts = cartMapper.findByUserId(loginSession.getTmpUserId());
			m.addAttribute("carts", carts);
		}

		m.addAttribute("loginSession", loginSession);
		
		return "cart";
	}

	/**
	 * カートに追加処理を行う
	 * @param f カート情報のForm
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/add")
	public String addCart(CartForm f, Model m) {
		int tempUserId = loginSession.getTmpUserId();
		int userId = loginSession.getUserId();
		
		TblCart cart = new TblCart();
		
		if(userId != 0) {
			cart.setUserId(loginSession.getUserId());
			cart.setProductId(f.getProductId());
			cart.setProductCount(f.getProductCount());
			int result = cartMapper.findCountByUserIdAndProuductId(loginSession.getUserId(), f.getProductId());
			if(result > 0) {
				cartMapper.update(cart);
			} else {
				cartMapper.insert(cart);
			}
			cartMapper.updateUserId(userId, tempUserId);
			List<CartDto> carts = cartMapper.findByUserId(loginSession.getUserId());
			m.addAttribute("carts", carts);
		} else {
			cart.setUserId(loginSession.getTmpUserId());
			cart.setProductId(f.getProductId());
			cart.setProductCount(f.getProductCount());
			int result = cartMapper.findCountByUserIdAndProuductId(loginSession.getTmpUserId(), f.getProductId());
			if(result > 0) {
				cartMapper.update(cart);
			} else {
				cartMapper.insert(cart);
			}
			List<CartDto> carts = cartMapper.findByUserId(loginSession.getTmpUserId());
			m.addAttribute("carts", carts);
		}

		m.addAttribute("loginSession", loginSession);
		
		return "cart";
	}

	/**
	 * カート情報を削除する
	 * @param checkedIdList 選択したカート情報のIDリスト
	 * @return true:削除成功、false:削除失敗
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/delete")
	@ResponseBody
	public boolean deleteCart(@RequestBody String checkedIdList) {		
		Map<String, List<Integer>> idMap = gson.fromJson(checkedIdList, Map.class);
		String key = "checkedIdList";
		List<Integer> checkedIds = idMap.get(key);
		int result = cartMapper.deleteById(checkedIds);
		if(result > 0) {
			return true;
		}else {
			return false;
		}		
	}
}
