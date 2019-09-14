package jp.co.allface.batch.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import sun.misc.BASE64Encoder;

/**
 * @className:      CommonUtilクラス
 * @description:  共通方法
 * @author:         高桐
 * @date:           2018/08/16
 */
public class CommonUtil {

    /**
     * @title:          checkMember
     * @description:  check会员编号是否8位
     * @param:          strCode  会员编号
     * @return:         boolean
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static boolean checkMember(String strCode) {
		strCode = strCode.trim();
		boolean result = false;
		if(strCode.length() == 8) {
			result = true;
		}
		return result;
	}

    /**
     * @title:          checkTicket
     * @description:  check票id是否20位
     * @param:          strCode   票id
     * @return:         boolean
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static boolean checkTicket(String strCode) {
		boolean result = false;
		if(strCode.length() == 20) {
			result = true;
		}
		return result;
	}

    /**
     * @title:          checkFaceImgName
     * @description:  check头像名称是否16位
     * @param:          strCode  头像名称
     * @return:         boolean
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static boolean checkFaceImgName(String strCode) {
		boolean result = false;
		if(strCode.length() == 16) {
			result = true;
		}
		return result;
	}

    /**
     * @title:          GetImageStrFromPath
     * @description:
     * @param:          imgPath  图片路径
     * @return:         Base64编码的文件
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static String GetImageStrFromPath(String imgPath) {
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgPath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		// 返回Base64编码过的字节数组字符串
		return encoder.encode(data);
	}

    /**
     * @title:          checkReadyTwo
     * @description:    check事先准备的文件是否存在，文件夹是否为空
     * @param:          inCsvPath   csv文件路径
     *                  imgPath   头像文件夹路径
     * @return:         boolean
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static boolean checkCsvAndImgFolder(File inCsvPath, File imgPath, Logger logger, String strObject) {
		boolean result = true;
		if(!inCsvPath.exists()) {
			logger.warn(strObject + "の年パス会員の「会員コード」と「顔画像ファイル名」が記載されたファイル(CSV形式カンマ区切り)が見つかりません。"+'\n'+"処理が中止しました。");
			result = false;
		}
		File[] listFiles = imgPath.listFiles();
		if(listFiles.length == 0){
			logger.warn("顔画像が格納されたフォルダに画像が見つかりません。"+'\n'+"処理が中止しました。");
			result = false;
		}
		return result;
	}
}
