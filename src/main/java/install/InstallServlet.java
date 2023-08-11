package install;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.DataAccessClientHolder;
import jp.co.interfactory.ebisu.app.customize.CustomizeApiFunction;
import jp.co.interfactory.ebisu.app.dataaccess.AccessTokenResponse;
import jp.co.interfactory.ebisu.app.dataaccess.DataAccessClient;
import jp.co.interfactory.ebisu.app.dataaccess.model.CustomizeApi;
import jp.co.interfactory.ebisu.app.dataaccess.service.EbisumartService;
import jp.co.interfactory.ebisu.app.dataaccess.service.InstallerService;

/**
 * Servlet implementation class InstallServlet
 */
@WebServlet(name="InstallServlet",urlPatterns={"/install"})
public class InstallServlet extends HttpServlet {
  private static String STATE_DATA = "CheckXSRF"; // 本来はXSRF対策としてチェックするためにワンタイムの情報を設定してください
//  private String APP_API_HOST = "https://demo-service-api.ebisumart.com/app_lib_test"; // アプリのルートURLです。
  private String APP_API_HOST = "http://localhost:8080/app-install-demo"; // アプリのルートURLです。

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      request.setCharacterEncoding("UTF-8");
      String req = request.getParameter("request");
      String shopId = request.getParameter("SHOP_ID");
      System.out.println(request.getRequestURI());
      
      if (req.equals("install")) {
          // 「install」ボタンが押されたので、oauthの認証エンドポイントにリダイレクト
          StringBuilder nextUrl = new StringBuilder();
          nextUrl.append(DataAccessClientHolder.get().getShopRootUrlForScreen(shopId) + "admin_authorize.html");
          nextUrl.append("?response_type=code");
          nextUrl.append("&client_id=").append(DataAccessClientHolder.getAppCd());
          nextUrl.append("&redirect_uri=").append(URLEncoder.encode(getRedirectUri(shopId), "UTF-8"));
//          nextUrl.append("&redirect_uri=").append(URLEncoder.encode("http://localhost/preparation/ajax/external_services/ebisumart/ebisumart_oauth", "UTF-8"));

          nextUrl.append("&scope=item privacy system");
          nextUrl.append("&state=").append(URLEncoder.encode(STATE_DATA, "UTF-8"));
          response.sendRedirect(nextUrl.toString());
          return;
      }else if(req.equals("authorized")) {
          // 認証を受けてauthorization code（もしくはエラー情報）が送られてきた 
          String msg = req + " complete";
          String code = request.getParameter("code");
          String state = request.getParameter("state");
          String error = request.getParameter("error");
          // stateが一致しない場合、errorがnullでない場合（不許可も含まれる）、エラー。
          if (!STATE_DATA.equals(state) || error != null) {
              msg = (error != null) ? (req + " " + error) : (req + " error");
              response.getWriter().println(msg);
              return;
          } 
          try {
              saveTokens(shopId, code);
              install(request.getParameter("SHOP_ID"));
          } catch (Exception e) {
              e.printStackTrace();
              msg = req + " error";
          }
          response.getWriter().println(msg);
          return;
      }else{
          response.getWriter().println(req + " error");
      }
  }

  /**
   * アクセストークン、リフレッシュトークンを保持します。
   * @param shopId えびすマートNo.
   * @param code 認証コード
   * @throws IOException
   */
  private void saveTokens(String shopId, String code) throws IOException {
      // 抽象クラスDataAccessClientを実装したクラスを取得します。
      // 環境に合わせたDataAccessClientの実装を行っていただく必要があります。
      DataAccessClient client = DataAccessClientHolder.get();
      // えびすマートへの認証要求時に渡したものと同じリダイレクト先URLです。
      String redirectUrl = getRedirectUri(shopId);
      // アクセストークン、リフレッシュトークンの取得を行います。
      AccessTokenResponse atr = client.requestAccessToken(shopId, code, redirectUrl);
      // リフレッシュトークンを保存します。アクセストークンの期限が切れ、再取得を行う際に使用します。
      DataAccessClientHolder.saveRefreshToken(atr.getShopId(), atr.getRefreshToken());
  }
  
  /**
   * インストール処理を行います。
   * 処理カスタマイズAPIの登録、管理画面カスタマイズAPIの登録を行います。
   * @param shopId えびすマートNo
   * @throws Exception
   */
  protected void install (String shopId) throws Exception {
      // 抽象クラスDataAccessClientを実装したクラスを取得します。
      // 環境に合わせたDataAccessClientの実装を行っていただく必要があります。
      DataAccessClient client = DataAccessClientHolder.get();
      // EbisumartServiceはデータアクセスAPI共通で使用します。
      EbisumartService es = new EbisumartService(shopId, client);
      // InstallerServiceは処理カスタマイズAPI、管理画面カスタマイズAPIの登録に使用します。
      InstallerService is = es.getInstallerService();
      // 登録したい処理カスタマイズAPIの情報をCustomizeApiに設定します。
      CustomizeApi ca = new CustomizeApi();
      ca.setFlowName("*");
      ca.setName(CustomizeApiFunction.Userweb_Cart_CustomizeOrderDetailData_After.path());
      ca.setUrl(APP_API_HOST + "/CartCustomizeOrderDetailData");
      
      // えびすマートへの登録開始
      // データアクセスAPIのトランザクションを開始します。
      es.startTransaction();
      try {
          // 一度、このアプリが登録していた処理カスタマイズAPIをクリア
          is.deleteCustomizeApi();
          // 処理カスタマイズAPI登録
          is.add(ca);
          // トランザクションのコミット
          es.commitTransaction();
      } catch (Exception e) {
          // トランザクションのロールバック
          es.rollbackTransaction();
      } finally {
          // トランザクションリソースの開放
          es.endTransaction();
      }
  }
  
  /**
   * えびすマートへの認証要求時に渡すリダイレクトURLを取得
   * @param shopId えびすマートNo
   * @return リダイレクト先URL
   */
  private String getRedirectUri(String shopId) {
      StringBuilder redirectUrl = new StringBuilder();
      redirectUrl.append(APP_API_HOST + "/install");
      redirectUrl.append("?request=authorized");
      redirectUrl.append("&SHOP_ID=").append(shopId);
      System.out.println(redirectUrl.toString());
      return redirectUrl.toString();
  }
}
