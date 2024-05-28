package jp.co.internous.team2403.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.team2403.model.domain.MstDestination;
import jp.co.internous.team2403.model.mapper.MstDestinationMapper;
import jp.co.internous.team2403.model.mapper.TblCartMapper;
import jp.co.internous.team2403.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.team2403.model.session.LoginSession;

/**
 * 決済に関する処理を行うコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2403/settlement")
public class SettlementController {
	
	/*
	 * フィールド定義
	 */
	@Autowired
	private MstDestinationMapper destinationMapper;
	@Autowired
	private LoginSession loginSession;
	@Autowired
	private TblPurchaseHistoryMapper tblPurchaseHistoryMapper;
	@Autowired
	private TblCartMapper cartMapper;
	
	private Gson gson = new Gson();
	
	/**
	 * 宛先選択・決済画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return 宛先選択・決済画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		List<MstDestination> destinations = destinationMapper.findByUserId(loginSession.getUserId());
		m.addAttribute("destinations", destinations);
		m.addAttribute("loginSession", loginSession);
		
		return "settlement";
	}
	
	/**
	 * 決済処理を行う
	 * @param destinationId 宛先情報id
	 * @return true:決済処理成功、false:決済処理失敗
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {		
		Map<String, String> destinationMap = gson.fromJson(destinationId, Map.class);
		String key = "destinationId";
		int destinationNum = Integer.parseInt(destinationMap.get(key));
		
		int result = tblPurchaseHistoryMapper.insert(destinationNum,loginSession.getUserId());
		if(result > 0) {
			int i = cartMapper.deleteByUserId(loginSession.getUserId());
			if(i > 0) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}		
	}
}
