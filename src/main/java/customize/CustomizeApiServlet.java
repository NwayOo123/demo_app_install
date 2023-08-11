package customize;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.DataAccessClientHolder;
import net.arnx.jsonic.JSON;
import jp.co.interfactory.ebisu.app.customize.CustomizeApiRequest;
import jp.co.interfactory.ebisu.app.customize.CustomizeApiResponse;

/**
 * 処理カスタマイズAPIの要求に対する処理を行う抽象クラスです。
 * ebisu_app_lib-1.3に対応。
 * @author Interfactory
 */
public abstract class CustomizeApiServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		
		// アプリケーションのパスワード
		String appPass = DataAccessClientHolder.getAppPass();
		// 処理カスタマイズAPIの処理を実行
		CustomizeApiResponse apiRes = execute(new CustomizeApiRequest(request, appPass));
		// 結果を返却します。
		respondFromReturnValue(response, apiRes);
	}
	
	/**
	 * 処理カスタマイズAPIの処理を実行
	 * @param req 処理カスタマイズAPIのリクエストデータ
	 * @return 処理カスタマイズAPIの処理結果
	 */
	protected abstract CustomizeApiResponse execute(CustomizeApiRequest req);
	
	/**
	 * レスポンス値をJSONに変換して設定します。
	 * @param response
	 * @param ret
	 * @throws IOException
	 */
	void respondFromReturnValue(HttpServletResponse response, Object ret) throws IOException {
		response.setCharacterEncoding("UTF-8");
		if (ret != null) {
			PrintWriter writer = response.getWriter();
			if (ret instanceof String) {
				writer.write((String) ret);
			} else {
				JSON.encode(ret, writer);
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
