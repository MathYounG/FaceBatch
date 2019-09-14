package jp.co.allface.batch.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import jp.co.allface.batch.entity.AllFaceBatchIniEntity;
import jp.co.allface.batch.util.CommonUtil;
import jp.co.allface.batch.util.HttpUtil;
import jp.co.allface.batch.util.IniRwUtil;

/**
 * @className:      AllFaceChkMain
 * @description:    顔一括チェックバッチ
 * @author:         高桐
 * @date:           2018/08/16
 */
public class AllFaceChkMain {
	// クラスAllFaceChkMainのログ処理対象
	private static final Logger logger = LoggerFactory.getLogger(AllFaceChkMain.class);

    /**
     * @title:          AllFaceCheck
     * @description:    顔一括チェック処理
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static void doAllFaceChk() {
		// 顔認証APIからの結果を内部保持する対象
		List<String> csvOutList = new ArrayList<String>();

		try {
			// 1 バッチ実行開始
			logger.info("顔一括チェックバッチの実行を開始する。");

			// 2 从Windows用设定文件rapeBatch.ini，检查配置并取得。。
			AllFaceBatchIniEntity entity = new AllFaceBatchIniEntity();
			if(!IniRwUtil.checkAndReadIni("AllFaceCheck", entity, logger)) {
	            return;
			}

			// 事前に準備したCSVファイル
			File fCsvIn = new File(entity.getStrCsvInputPath());
			// 事前に準備した顔画像が格納されたフォルダ
			File fImgFolder = new File(entity.getStrImageFolder());
			// 3 判断事先准备的CSV文件是否存在。 
			// 4 判断脸部图像是否在存储的文件夹中是否有图像
	        if (!CommonUtil.checkCsvAndImgFolder(fCsvIn, fImgFolder, logger, "顔一括チェック対象")) {
	        	return;
	        }

	        // 5 读取事先准备的CSV文件，并将各行记载的“会员代码”、“票ID”、“脸部图像文件名”全部保持在内部。 
			// 事前に準備したCSVファイルの内容を格納する対象
			List<String> lstCsvContent = new ArrayList<String>();
			// 输入流对象 
			InputStreamReader isr = new InputStreamReader(new FileInputStream(fCsvIn), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			// CSVファイルの読み込みライン
			String strLine = "";
			//　事前に準備したCSVファイルを読み込み、lstCsvContent対象に格納する。
			while((strLine = br.readLine()) != null) {
				// lstCsvContent対象にファイルのラインを追加する。
				lstCsvContent.add(strLine);
			}
			br.close();
			isr.close();

	        // 6 在内部保持的全部“会员代码”、“票ID”、“脸部图像文件名”的数据的行进行循环，进行下面的处理。 
			// 顔画像ファイル名
			String strFaceFileName = "";
	        // 事前に準備した顔画像が格納されたフォルダのすべて顔画像ファイル名
	    	String[] imgArr = fImgFolder.list();
	    	// lstCsvContent対象をループし、下記の処理を行う。
			for(int i = 0; i < lstCsvContent.size(); i++) {
				// 6-1 チェックフラグbCheckをtrueに設定する。
				boolean bCheck = true;

				//　lstCsvContent対象から(i+1)行目データを取得する。
				String[] dataArr = lstCsvContent.get(i).split(",");
				// 会員コード取得
				String strMemberCode = dataArr[0];
				// 6-2 将下面的消息显示在编译中，并记录在记录文件中。
				logger.info("会員" +strMemberCode +"の関連データのチェックを開始する。");

				// 6-3 进行下面的检查。
				// a.該当行にカンマは二つであるかどうかを判断する。
				if(dataArr.length == 3) {
					// b.「会員コード」は8桁であるかどうかを判断する。
					if(!CommonUtil.checkMember(strMemberCode)) {
						// 8桁ではない場合、下記のメッセージをコンソールに表示し、ログファイルに記録し、チェックフラグbCheckをfalseに設定する。
						logger.error("会員コード" +strMemberCode+"の桁数は不正です。(" +(i+1)+"行)");
						bCheck = false;
					}

					// c.「チケットID」は20桁であるかどうかを判断する。
					String ticketId = dataArr[1];
					if(!CommonUtil.checkTicket(ticketId)) {
						// 20桁ではない場合、下記のメッセージをコンソールに表示し、ログファイルに記録し、チェックフラグbCheckをfalseに設定する。
						logger.error("チケットID"+ticketId+"の桁数は不正です。("+(i+1)+"行)");
						bCheck = false;
					}

					// 顔画像のフールファイル名
					strFaceFileName = dataArr[2];
					// 顔画像のファイル名を取得する。
					String strImgName = "";
					if(strFaceFileName.contains(".")) {
						strImgName = strFaceFileName.split(".")[0];
					}
					// d.「顔画像ファイル名」は16桁であるかどうかを判断する。
					if(!CommonUtil.checkFaceImgName(strImgName)) {
						// 16桁ではない場合、下記のメッセージをコンソールに表示し、ログファイルに記録し、チェックフラグbCheckをfalseに設定する。
						logger.error("顔画像ファイル名"+strFaceFileName+"の桁数は不正です。("+(i+1)+"行");
						bCheck = false;
					}else if(!strMemberCode.equals(strImgName.substring(0, 8))) {
						// e.「顔画像ファイル名」の頭8桁が「会員コード」と一致であるかどうかを判断する。
						// 一致ではない場合、下記のメッセージをコンソールに表示し、ログファイルに記録し、チェックフラグbCheckをfalseに設定する。
						logger.error("顔画像ファイル名"+strFaceFileName+"の頭8桁が会員コード" +strMemberCode +"と一致ではない。("+(i+1)+"行)");
						bCheck = false;
					}

					// f.顔画像が格納されたフォルダに、「顔画像ファイル名」の画像が存在であるかどうかを判断する。
					if(!Arrays.asList(imgArr).contains(strFaceFileName)) {
						// 存在しない場合、下記のメッセージをコンソールに表示し、ログファイルに記録し、チェックフラグbCheckをfalseに設定する。
						logger.error("顔画像ファイル"+strFaceFileName+"が見つかりません。");
						bCheck = false;
					}
				}else {
					// 二つではない場合、下記のメッセージをコンソールに表示し、ログファイルに記録し、チェックフラグbCheckをfalseに設定する。
					logger.error("顔一括チェック対象の年パス会員の「会員コード」、「チケットID」、「顔画像ファイル名」が記載されたファイルはCSV形式カンマ区切りではない。(" +(i+1)+"行)");
					bCheck = false;
				}

				// 6-4 将下面的消息显示在编译中，并记录在记录文件中。
				logger.info("会員" +strMemberCode +"の関連データのチェックを終了する。");

				// 6-5 判断标记bCheck的值。 。
				if(bCheck) {
					// bCheckの値はtrueの場合、「6-6」～「6-9」の処理を行う。
					// 6-6 下記のメッセージをコンソールに表示し、ログファイルに記録する。
					logger.info("顔画像ファイル" +strFaceFileName +"の顔検出処理を開始する。");

					// 6-7 顔画像データをURLセーフなBase64エンコーディングで文字列化して、顔検出APIをたたく。
					//调用顔検出API
//							String strUrl = iniRead.getValue("AllFaceCheck", "allFaceChkUrl");
//							String strPostData = strImgFolder + "/"+strPic;
//							HttpURLConnection huc = HttpUtil.sendRequestPost(strUrl, strPostData);
//							int iCode = huc.getResponseCode();
//							String strMeg = huc.getResponseMessage();
//							String strHttp = ""+iCode+":"+strMeg;
//							JsonObject jObject = HttpUtil.getResponseJson(huc);
//							String strStatus =  jObject.get("status").toString();
//							String strCode =  jObject.get("statusCode").toString();
//							String strMessage =  jObject.get("statusMessage").toString();
//							String strOutLine = csvList.get(i) + "," +strHttp+"," +strStatus+","+strCode+","+strMessage;

					// 6-8 将下面的信息记录到记录文件
					logger.info("顔画像ファイル" +strFaceFileName +"の顔検出処理を終了する。");

					// 6-9 内部保持来自面部认证API的结果
					csvOutList.add(strFaceFileName);
				}else {
					// 如果bCheck的值是falk的话，就跳过“6-6”～“6-9”的处理，进行“6-1”的处理。 
					continue;
				}
			}

			// 7 通过文件（CSV形式分段）输出内部保持的数据。 
			// 取得系统时间。
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String strTime = sdf.format(new Date());
			String strOutPath = entity.getStrCsvOutputPrefix() + "_" + strTime;

			// 7-1 将下面的消息显示在编译中，并记录在记录文件中
			logger.info("出力ファイル" + strOutPath + ".csv" + "の作成を開始する。");

			// 7-2 输出图像 
			// 会员代码，面部图像文件名，HTTP状态，认证状态，认证状态代码，认证状态信息
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:/" + strOutPath + ".csv")));
			bw.write("会員コード,チケットID,顔画像ファイル名,HTTPステータス,認証ステータス,認証ステータスコード,認証ステータスメッセージ");
			bw.newLine();
			for(String csv : csvOutList) {
				// 内容出力
				bw.write(csv);
				bw.newLine();
			}
			bw.close();

			// 7-3 下記のメッセージをコンソールに表示し、ログファイルに記録する。
			logger.info("出力ファイル" + strOutPath + ".csv" + "の作成を終了する。");

			// 8 バッチ実行終了
			// 下記のメッセージをコンソールに表示し、ログファイルに記録する。
			logger.info("顔一括チェックバッチの実行を終了する。");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
