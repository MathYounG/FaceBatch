package jp.co.allface.batch.util;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @className:      HttpUtilクラス
 * @description:    HTTPプロトコルのツールのクラス
 * @author:         高桐
 * @date:           2018/08/16
 */
public class HttpUtil {

    /**
     * @title:          sendRequestPost
     * @description:    POSTメソッドのリクエスト処理
     * @param:          strUrl         HTTPリクエストのURL
     *                  strPostData    リクエストデータ
     * @return:         HttpURLConnection対象
     * @author:         高桐
     * @date:           2018/08/16
     */
    public static HttpURLConnection sendRequestPost(String strUrl,String strPostData){
        URL url = null;
        try {
            url = new URL(strUrl);
            // HttpURLConnection対象の作成
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // POSTメソッドの設定
            httpURLConnection.setRequestMethod("POST");
            // コネクトのタイムアウトの設定
            httpURLConnection.setConnectTimeout(10000);
            // サーバーの結果の読み込みのタイムアウトの設定
            httpURLConnection.setReadTimeout(2000);
            // 出力設定
            httpURLConnection.setDoOutput(true);
            // 入力設定
            httpURLConnection.setDoInput(true);
            // HttpURLConnection対象の出力ストリーム対象を作成する。
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // リクエストのパラメータを設定する。
            printWriter.write(strPostData);
            printWriter.flush();

            return httpURLConnection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @title:          getResponseJson
     * @description:    POSTメソッドのレスポンス処理
     * @param:          httpURLConnection    HttpURLConnection対象
     * @return:         JsonObject対象
     * @author:         高桐
     * @date:           2018/08/16
     */
    public static JsonObject getResponseJson(HttpURLConnection httpURLConnection){
        try {
            // APIからの結果を取得する。
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int iLen;
            byte[] byteArr = new byte[1024];
            while((iLen=bis.read(byteArr))!= -1){
                bos.write(byteArr,0,iLen);
                bos.flush();
            }
            bos.close();

            // APIからの結果をJSONに転換する。
            JsonParser parse = new JsonParser();
            return (JsonObject)parse.parse(bos.toString("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
