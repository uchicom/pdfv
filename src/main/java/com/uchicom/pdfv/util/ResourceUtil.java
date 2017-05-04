// (c) 2014 uchicom
package com.uchicom.pdfv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.uchicom.pdfv.Constants;

/**
 * リソースユーティリティークラス.
 *
 * @author Shigeki Uchiyama
 *
 */
public class ResourceUtil {

	/** リソースバンドル */
	public static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle("com.uchicom.pdfv.resource");

	/** プロパティ. */
	private static final Properties properties = new Properties();
	/**
	 * リソースから文字列を取得する.
	 *
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		String value = resourceBundle.getString(key);
		return value == null ? key : value;
	}

	/**
	 * リソースから文字列をカンマ区切りで配列で取得する.
	 *
	 * @param key
	 * @return
	 */
	public static String[] getStrings(String key) {
		return getStrings(key, ",");
	}

	/**
	 * リソースから文字列を配列で取得する.
	 *
	 * @param key
	 * @param separator
	 * @return
	 */
	public static String[] getStrings(String key, String separator) {
		String value = getString(key);
		return value.split(separator);
	}

	//初期化処理.
	static {
		//プロパティ処理
		FileInputStream fi = null;
		try {
			String jarPath = System.getProperty("java.class.path");
			String dirPath = jarPath.substring(0, jarPath.lastIndexOf(File.separator)+1);
			File file = new File(dirPath + "config.properties");
			if (!file.exists()) {
				file.createNewFile();
			}
			fi = new FileInputStream(file);
			properties.load(fi);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fi != null) {
				try {
					fi.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private static final boolean INFO = "info".equals(properties.getProperty(Constants.LOG));
	private static final boolean DEBUG = INFO || "debug".equals(properties.getProperty(Constants.LOG));
	/**
	 * デバッグログ出力.
	 * @param message
	 */
	public static void debug(Object message) {
		if (DEBUG) {
			System.out.println(message);
		}
	}

	/**
	 * インフォログ出力.
	 * @param message
	 */
	public static void info(Object message) {
		if (INFO) {
			System.out.println(message);
		}
	}

}
