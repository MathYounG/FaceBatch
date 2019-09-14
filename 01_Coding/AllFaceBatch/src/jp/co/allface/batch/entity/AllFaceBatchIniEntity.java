package jp.co.allface.batch.entity;

/**
 * @className:      AllFaceBatchIniEntityクラス
 * @description:    Windows用設定ファイルallFaceBatch.iniのエンティティ
 * @author:         高桐
 * @date:           2018/08/16
 */
public class AllFaceBatchIniEntity {
    // 顔画像が格納されたフォルダ
	private String strImageFolder;
    // 事前に準備したCSVファイル
	private String strCsvInputPath;
    // 出力ファイルの先頭綴り
	private String strCsvOutputPrefix;
    // APIのURL
	private String strRequestUrl;

	public String getStrImageFolder() {
		return strImageFolder;
	}

	public void setStrImageFolder(String strImageFolder) {
		this.strImageFolder = strImageFolder;
	}

	public String getStrCsvInputPath() {
		return strCsvInputPath;
	}

	public void setStrCsvInputPath(String strCsvInputPath) {
		this.strCsvInputPath = strCsvInputPath;
	}

	public String getStrCsvOutputPrefix() {
		return strCsvOutputPrefix;
	}

	public void setStrCsvOutputPrefix(String strCsvOutputPrefix) {
		this.strCsvOutputPrefix = strCsvOutputPrefix;
	}

	public String getStrRequestUrl() {
		return strRequestUrl;
	}

	public void setStrRequestUrl(String strRequestUrl) {
		this.strRequestUrl = strRequestUrl;
	}
}
