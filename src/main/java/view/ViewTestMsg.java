package view;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import jp.co.interfactory.ebisu.app.viewaddon.ViewAddonRequest;

/**
 * ビューアドオンのサンプルです。
 * ebisu_app_lib-1.3に対応。
 * @author Interfactory
 */
@WebServlet(name="ViewTestMsg",urlPatterns={"/view/ViewTestMsg"})
public class ViewTestMsg extends ViewApiServlet {
	
	/**
	 * レスポンス用オブジェクト
	 */
	static class ResponseElement {
		public String testMsg;
		public List<Msg> msgList;
	}
	
	static class Msg {
		public String testMsg2;
		Msg(String msg) {
			this.testMsg2 = msg;
		}
	}
	
	@Override
	protected ResponseElement execute(ViewAddonRequest req) {
		ResponseElement result = new ResponseElement();
		result.testMsg = "テンプレートでtestMsgを指定するとこの文字列が出力されます。";
		
		result.msgList = new ArrayList<Msg>();
		result.msgList.add(new Msg("テンプレートでtestMsg2を指定するとこの文字列が出力されます。1"));
		result.msgList.add(new Msg("テンプレートでtestMsg2を指定するとこの文字列が出力されます。2"));
		return result;
	}
}
