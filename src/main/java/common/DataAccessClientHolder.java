package common;

import java.util.HashMap;
import java.util.Map;

import jp.co.interfactory.ebisu.app.dataaccess.DataAccessClient;

/**
 * DataAccessClientを保持するクラスです。
 * ebisu_app_lib-1.3に対応。
 * @author Interfactory
 */
public class DataAccessClientHolder {
	private static final String APP_CD = "OFFSHORE_09082023"; // アプリコード
	public static String getAppCd() {return APP_CD;}
	private static final String APP_PASS = "b333c5f41a302077330a103448a047b7"; // アプリパスワード
	public static String getAppPass() {return APP_PASS;}
	private static String EBISU_API_HOST = "https://demo-admin.ebisumart.com/"; // えびすマート（API呼び出し）のルートURLです。
	private static String EBISU_SCREEN_HOST = "https://demo-admin.ebisumart.com/"; // えびすマート（認証コード取得用）のルートURLです。
	
	private static volatile DataAccessClient client = null;
	// 実際はリフレッシュトークンはDB等に保持すべきですが、この例ではMapに持っています
	private static final Map<String, String> refreshTokenMap = new HashMap<String, String>();
	
	public static synchronized void initialize() {
		client = new DataAccessClient(getAppCd(), getAppPass()) {
			@Override
			public String getRefereshToken(String shopId) {
				// TODO 指定されたshopId（えびすマートNo）に対するリフレッシュトークンを返すように実装してください。
				return refreshTokenMap.get(shopId);
			}
			@Override
			public String getShopRootUrlForAPI(String shopId) {
				return getShopRootUriForApi(shopId);
			}
			@Override
			public String getShopRootUrlForScreen(String shopId) {
				return getShopRootUriForScreen(shopId);
			}
		};
	}
	
	private static String getShopRootUriForApi(String shopId) {
		// TODO 指定されたshopId（えびすマートNo）に対するAPIコール用のルートURLを返すように実装してください。
		return EBISU_API_HOST + shopId + "/";
	}
	
	private static String getShopRootUriForScreen(String shopId) {
		// TODO 指定されたshopId（えびすマートNo）に対するブラウザアクセス用のルートURLを返すように実装してください。
		return EBISU_SCREEN_HOST + shopId + "/";
	}
	
	/**
	 * リフレッシュトークンを保存します。
	 * @param shopId えびすマートNo
	 * @param refreshToken
	 */
	public static void saveRefreshToken(String shopId, String refreshToken) {
		refreshTokenMap.put(shopId, refreshToken);
	}
	
	/**
	 * 保持しているDataAccessClientを返します。
	 * まだDataAccessClientが作成されていない場合、作成します。
	 * @return DataAccessClient
	 */
	public static DataAccessClient get() {
		if (client == null) {
			initialize();
		}
		return client;
	}
}
