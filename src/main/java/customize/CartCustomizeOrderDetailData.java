package customize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import jp.co.interfactory.ebisu.app.customize.CustomizeApiRequest;
import jp.co.interfactory.ebisu.app.customize.CustomizeApiResponse;
import jp.co.interfactory.ebisu.app.dataaccess.model.CartItem;

/**
 * カートでの注文時に受注明細に登録する情報を設定する際に呼ばれる処理です。
 * ebisu_app_lib-1.3に対応。
 * @author Interfactory
 */
@WebServlet(name="CartCustomizeOrderDetailData",urlPatterns={"/CartCustomizeOrderDetailData"})
public class CartCustomizeOrderDetailData extends CustomizeApiServlet {

	@Override
	protected CustomizeApiResponse execute(CustomizeApiRequest req) {
		CustomizeApiResponse res = new CustomizeApiResponse();
		@SuppressWarnings("unchecked")
		Map<String, String> orderD = (Map<String, String>)req.getArg("order_detail");
		String itemId = orderD.get("ITEM_ID");
		
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>)req.getCurrentResult(); 
		if (map == null) {
			map = new HashMap<String, String>();
		}
		List<CartItem> cartList = req.getCartItems();
		String freeItem1 = null;
		for(CartItem cartItem: cartList) {
			String id = cartItem.getItemIdString();
			if (!(itemId == null) && itemId.equals(id)) {
				// cartオブジェクトから商品の自由項目1を取得
				freeItem1 = cartItem.getFreeItem(1);
				break;
			}
		}
		// 受注明細の自由項目1に商品の自由項目1から取り出した値を設定
		map.put("FREE_ITEM1", freeItem1);
		res.setResult(map);
		
		return res;
	}
}
