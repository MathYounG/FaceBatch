package jp.co.allface.batch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.co.allface.batch.entity.AllFaceBatchIniEntity;
import jp.co.allface.batch.main.AllFaceChkMain;
import jp.co.allface.batch.main.AllFaceRegMain;
/**
 * @className:      IniRwUtilクラス
 * @description:    读取ini配置文件
 * @author:         高桐
 * @date:           2018/08/16
 */
public class IniRwUtil {
	protected static HashMap<String, Properties> sections = new HashMap<String, Properties>();
	private transient String strCurrentSection;
	private transient Properties current;
	private static final String STR_INI_PATH = "./conf/allFaceBatch.ini";

    /**
     * @title:          read
     * @description:  构造函数
     * @author:         高桐
     * @date:           2018/08/16
     */
	public IniRwUtil() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(STR_INI_PATH));
		read(reader);
		reader.close();
	}

    /**
     * @title:          read
     * @description:  读取每行数据
     * @param:          reader    BufferedReader对象
     * @author:         高桐
     * @date:           2018/08/16
     */
	protected void read(BufferedReader reader) throws IOException {
		String strLine;
		while ((strLine = reader.readLine()) != null) {
			parseLine(strLine);
		}
	}

    /**
     * @title:          parseLine
     * @description:  每行数据按键值对存储在Properties中
     * @param:          strLine
     * @author:         高桐
     * @date:           2018/08/16
     */
	protected void parseLine(String strLine) {
		strLine = strLine.trim();
		if (strLine.matches("\\[.*\\]")) {
			strCurrentSection = strLine.replaceFirst("\\[(.*)\\]", "$1");
			current = new Properties();
			sections.put(strCurrentSection, current);
		} else if (strLine.matches(".*=.*")) {
			if (current != null) {
				int i = strLine.indexOf('=');
				String name = strLine.substring(0, i);
				String value = strLine.substring(i + 1);
				current.setProperty(name, value);
			}
		}
	}

    /**
     * @title:          getValue
     * @description:    获取属性值
     * @param:          section         section值
     *                  name    key
     * @return:         属性值
     * @author:         高桐
     * @date:           2018/08/16
     */
	public  String getValue(String section, String name) {
		Properties properties = (Properties) sections.get(section);
		if (properties == null) {
			return null;
		}
		String value = properties.getProperty(name);
		return value;
	}

    /**
     * @title:          checkAndReadIni
     * @description:    Windows用設定ファイルallFaceBatch.iniから、配置をチェックして取得する。
     * @param:          section  String  セクション
     *                  logger   Logger  ログ処理対象
     * @return:         boolean(true:チェック成功、false:チェック失敗)
     * @author:         高桐
     * @date:           2018/08/16
     */
	public static boolean checkAndReadIni(String section, AllFaceBatchIniEntity entity, Logger logger) throws IOException {
		// 2-1 Windows用設定ファイルallFaceBatch.iniが存在するかどうかを判断する。
		File fAllFaceBatchIni = new File(STR_INI_PATH);
		if(!fAllFaceBatchIni.exists()) {
			// 存在しない場合、下記のメッセージをコンソールに表示し、ログファイルに記録し、処理を終了する。
			logger.warn("Windows用設定ファイルallFaceBatch.iniが見つかりません。"+'\n'+"処理が中止しました。");
			return false;
		}

		IniRwUtil iniRwUtil = new IniRwUtil();
		// 2-2 Windows用設定ファイルallFaceBatch.iniに[AllFaceCheck]の「ImageFolder」の値を取得する。
		String strImageFolder = iniRwUtil.getValue(section, "ImageFolder");
		if(strImageFolder == null || strImageFolder == "") {
			logger.warn("Windows用設定ファイルallFaceBatch.iniに["+section+"]の「ImageFolder」の値の取得を失敗しました。"+'\n'+"処理が中止しました。");
			return false;
		}else {
			entity.setStrImageFolder(strImageFolder);
		}

		// 2-3 Windows用設定ファイルallFaceBatch.iniに[AllFaceCheck]の「CsvInputPath」の値を取得する。
		String strCsvInputPath = iniRwUtil.getValue(section, "CsvInputPath");
		if(strCsvInputPath == null || strCsvInputPath == "") {
			logger.warn("Windows用設定ファイルallFaceBatch.iniに["+section+"]の「CsvInputPath」の値の取得を失敗しました。"+'\n'+"処理が中止しました。");
			return false;
		}else {
			entity.setStrCsvInputPath(strCsvInputPath);
		}

		// 2-4 Windows用設定ファイルallFaceBatch.iniに[AllFaceCheck]の「CsvOutputPrefix」の値を取得する。
		String strCsvOutputPrefix = iniRwUtil.getValue(section, "CsvOutputPrefix");
		if(strCsvOutputPrefix == null || strCsvOutputPrefix == "") {
			logger.warn("Windows用設定ファイルallFaceBatch.iniに["+section+"]の「CsvOutputPrefix」の値の取得を失敗しました。"+'\n'+"処理が中止しました。");
			return false;
		}else {
			entity.setStrCsvOutputPrefix(strCsvOutputPrefix);
		}

		// 2-5 Windows用設定ファイルallFaceBatch.iniに[AllFaceCheck]の「allFaceChkUrl」の値を取得する。
		String strRequestUrl = "";
		if("AllFaceCheck".equals(section)) {
			strRequestUrl = iniRwUtil.getValue(section, "allFaceChkUrl");
			if(strRequestUrl == null || strRequestUrl == "") {
				logger.warn("Windows用設定ファイルallFaceBatch.iniに["+section+"]の「allFaceChkUrl」の値の取得を失敗しました。"+'\n'+"処理が中止しました。");
				return false;
			}else {
				entity.setStrRequestUrl(strRequestUrl);
			}
		}else if("AllFaceRegistration".equals(section)) {
			strRequestUrl = iniRwUtil.getValue(section, "allFaceRegUrl");
			if(strRequestUrl == null || strRequestUrl == "") {
				logger.warn("Windows用設定ファイルallFaceBatch.iniに["+section+"]の「allFaceRegUrl」の値の取得を失敗しました。"+'\n'+"処理が中止しました。");
				return false;
			}else {
				entity.setStrRequestUrl(strRequestUrl);
			}
		}

		return true;
	}
}
