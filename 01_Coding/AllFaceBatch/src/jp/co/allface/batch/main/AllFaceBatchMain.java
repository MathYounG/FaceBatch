package jp.co.allface.batch.main;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * @className:      AllFaceBatchMain
 * @description:    バッチの入り口のクラス
 * @author:         高桐
 * @date:           2018/08/16
 */
public class AllFaceBatchMain{
    // クラスAllFaceBatchMainのログ処理対象
    private final static Logger logger = LoggerFactory.getLogger(AllFaceBatchMain.class);

    /**
     * @title:          main
     * @description:    バッチの入り口の関数
     * @param:          args  String[]  入力パラメーター
     * @author:         高桐
     * @date:           2018/08/16
     */
    public static void main(String[] args) throws IOException {
        // パラメータ数をチェックする。
        if(args.length == 1) {
            if("1".equals(args[0])) {
                // ログ設定ファイル読み込み
                SetLogBackXmlFile();
                // 顔一括チェックバッチを行う。
                AllFaceChkMain.doAllFaceChk();
            }else if("2".equals(args[0])) {
                // ログ設定ファイル読み込み
                SetLogBackXmlFile();
                // 顔一括登録バッチを行う。
                AllFaceRegMain.doAllFaceReg();
            }else {
            	// パラメータは1、2ではない場合、エラーログ出力
                logger.error("パラメータを1または2に設定してください。(1:顔一括チェックバッチ、2:顔一括登録バッチ)");
            }
        }else {
            // パラメータ数は1ではない場合、エラーログ出力
            logger.error("パラメータを1または2に設定してください。(1:顔一括チェックバッチ、2:顔一括登録バッチ)");
        }
    }

     /**
     * @title:          SetLogBackXmlFile
     * @description:    ログ設定ファイル読み込み
     * @author:         高桐
     * @date:           2018/08/16
     */
    private static void SetLogBackXmlFile() {
        // ログ設定ファイルを指定する。
        File logbackFile = new File("./conf/logback.xml");
        // ログ設定ファイルが存在する場合、ログ設定ファイル読み込み
        if (logbackFile.exists()) {
            try {
                // ログ設定ファイル読み込み
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                lc.reset();
                configurator.doConfigure(logbackFile);
            } catch (JoranException e) {
                e.printStackTrace(System.err);
                System.exit(-1);
            }
        }else {
            // ログ設定ファイルが存在しない場合、ログ出力
            logger.warn("ログ設定ファイルが見つかりません。\n処理が中止しました。");
        }
    }

}
