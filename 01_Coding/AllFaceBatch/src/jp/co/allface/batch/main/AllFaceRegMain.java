package jp.co.allface.batch.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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
 * @className:      AllFaceRegMain
 * @description:    顔一括登録バッチ
 * @author:         高桐
 * @date:           2018/08/16
 */
public class AllFaceRegMain {
	// クラスAllFaceRegMainのログ処理対象
	private final static Logger logger = LoggerFactory.getLogger(AllFaceRegMain.class);

    /**
     * @title:          AllFaceReg
     * @description:    顔一括登録処理
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static void doAllFaceReg() throws IOException{
		logger.info("顔一括登録バッチの実行を開始する。");
		//用于存放读取的数据
		List<String> csvList = new ArrayList<String>();
		//用于存放生成的数据
		List<String> csvOutList = new ArrayList<String>();
		//格式化时间
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		AllFaceBatchIniEntity entity = new AllFaceBatchIniEntity();
		if(IniRwUtil.checkAndReadIni("AllFaceRegistration", entity, logger)) {
			IniRwUtil iniRead = new IniRwUtil();
			String strCsvPath = iniRead.getValue("AllFaceRegistration", "CsvInputPath");
			File inCsvPath = new File(strCsvPath);
			String strImgFolder = iniRead.getValue("AllFaceRegistration", "ImageFolder");
			File imgPath = new File(strImgFolder);
			String strPic  =  "";
	        if (CommonUtil.checkCsvAndImgFolder(inCsvPath, imgPath, logger, "顔一括登録対象")) {
        		String[] imgArr = imgPath.list();
				//用于读取数据
        		InputStreamReader isr = new InputStreamReader(new FileInputStream(inCsvPath), "UTF-8");
        		BufferedReader br = new BufferedReader(isr);
				String strLine = "";
	    		//循环读取每一行
	    		while((strLine = br.readLine()) != null) {
	    			//有数据就存放至集合
	    			csvList.add(strLine);
	    		}
	    		isr.close();
	    		br.close();
				for(int i = 0;i<csvList.size();i++) {
					boolean bflag = true;
					//每一行数据分割成数组
					String[] dataArr = csvList.get(i).split(",");
					//获取会员编号
					String strMemberCode = dataArr[0];
					logger.info("会員" +strMemberCode +"の関連データのチェックを開始する。");
					//判断是否包含两个逗号
					if(dataArr.length == 3) {
						//获取票id
						String strTicketId = dataArr[1];
						if(!CommonUtil.checkMember(strMemberCode)) {
							logger.error("会員コード" +strMemberCode+"の桁数は不正です。(" +(i+1)+"行)");
							bflag = false;
						}
						if(!CommonUtil.checkTicket(strTicketId)) {
							logger.error("チケットID"+strTicketId+"の桁数は不正です。("+(i+1)+"行)");
							bflag = false;
						}
						//获取人脸图像名称
						strPic = dataArr[2];
						String strImgName = strPic.substring(0, 16);
						if(!CommonUtil.checkFaceImgName(strImgName)) {
							logger.error("顔画像ファイル名"+strPic+"の桁数は不正です。("+(i+1)+"行");
							bflag = false;
						}else {
							if(!strMemberCode.equals(strImgName.substring(0, 8))) {
								logger.error("顔画像ファイル名"+strPic+"の頭8桁が会員コード" +strMemberCode +"と一致ではない。("+(i+1)+"行)");
								bflag = false;
							}
						}
						if(!Arrays.asList(imgArr).contains(strPic)) {
							logger.error("顔画像ファイル"+strPic+"が見つかりません。");
							bflag = false;
						}
					}else {
						logger.error("顔一括登録対象の年パス会員の「会員コード」、「チケットID」、「顔画像ファイル名」が記載されたファイルはCSV形式カンマ区切りではない。(" +(i+1)+"行)");
						bflag = false;
					}
					logger.info("会員" +strMemberCode +"の関連データのチェックを終了する。");
					if(bflag) {
						logger.info("顔画像ファイル" +strPic +"の顔検出処理を開始する。");
						long startTime=System.currentTimeMillis();
						//调用顔検出API
						String strUrl = iniRead.getValue("AllFaceRegistration", "allFaceRegUrl");
						String strPostData = strImgFolder + "/"+strPic;
						HttpURLConnection huc = HttpUtil.sendRequestPost(strUrl, strPostData);
						int iCode = huc.getResponseCode();
						String strMeg = huc.getResponseMessage();
						String strHttp = ""+iCode+":"+strMeg;
						JsonObject jObject = HttpUtil.getResponseJson(huc);
						String strStatus =  jObject.get("status").toString();
						String strCode =  jObject.get("statusCode").toString();
						String strMessage =  jObject.get("statusMessage").toString();
						String strOutLine = csvList.get(i) + "," +strHttp+"," +strStatus+","+strCode+","+strMessage;
						long endTime=System.currentTimeMillis();
						logger.info("顔画像ファイル" +strPic +"の顔検出処理时间: "+(endTime-startTime)+"ms");
						logger.info("顔画像ファイル" +strPic +"の顔検出処理を終了する。");
						csvOutList.add(strOutLine);
					}else {
						continue;
					}
				}
	    		//定义生成的csv文件名
	    		String strTime = sdf.format(new Date());
	    		String strOutPath ="D:/"+iniRead.getValue("AllFaceRegistration", "CsvOutputPrefix")+"_" + strTime + ".csv";
	    		logger.info("出力ファイル"+iniRead.getValue("AllFaceRegistration", "CsvOutputPrefix")+"_"+strTime+"の作成を開始する。");
	    		//用于写入数据
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(strOutPath)));
				//写入列名
				bw.write("会員コード,チケットID,顔画像ファイル名,HTTPステータス,パラメータ");
				bw.newLine();
				for(String csv : csvOutList) {
					//写入到csv中
					bw.write(csv);
					bw.newLine();
				}
	    		//关闭
				bw.close();
	    		logger.info("出力ファイル"+iniRead.getValue("AllFaceRegistration", "CsvOutputPrefix")+"_"+strTime+"の作成を終了する。");
	        }else {
	        	return;
	        }
		}else {
			return;
		}
		logger.info("顔一括登録バッチの実行を終了する。");
	}


}
