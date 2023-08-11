package dataaccess;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.DataAccessClientHolder;
import jp.co.interfactory.ebisu.app.dataaccess.model.Item;
import jp.co.interfactory.ebisu.app.dataaccess.service.EbisumartService;
import jp.co.interfactory.ebisu.app.dataaccess.service.ItemsService;

@WebServlet(name="DataAccessServlet",urlPatterns={"/dataaccess"})
public class DataAccessServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String shopId = "えびすマートNo";
		EbisumartService es = new EbisumartService(shopId, DataAccessClientHolder.get());
		ItemsService is = es.getItemsService();
		Item item = is.getItemById("1");
		
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(item.getItemName());
	}
}
