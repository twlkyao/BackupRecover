package com.twlkyao.backuprecover;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.twlkyao.utils.ConstantVariables;
import com.twlkyao.utils.LogUtils;
import com.twlkyao.utils.SmsField;
import com.twlkyao.utils.SmsItem;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;

public class ImportSms {

	private boolean debug = true;
	private String tag = "ImportSms";
	private LogUtils logUtils = new LogUtils(debug, tag);
	public static final String SMS_URI_ALL = "content://sms/"; // Sms uri.
	private Context context;
	private List<SmsItem> smsItems;
	private ContentResolver conResolver;
	
	public ImportSms(Context context) {
		this.context = context;
		conResolver = context.getContentResolver();
	}

	public void InsertSMS() {
		/**
		 * 放一个解析xml文件的模块
		 */
		smsItems = this.getSmsItemsFromXml();
		
		logUtils.d(tag, "InsertSms");
		
		for (SmsItem item : smsItems) {

			Uri uri = Uri.parse(SMS_URI_ALL);
			// 判断短信数据库中是否已包含该条短信，如果有，则不需要恢复
			Cursor cursor = conResolver.query(uri, new String[] { SmsField.DATE }, SmsField.DATE + "=?",
					new String[] { item.getDate() }, null);

			if (!cursor.moveToFirst()) {// 没有该条短信
				
				ContentValues values = new ContentValues();
				values.put(SmsField.ADDRESS, item.getAddress());
				// 如果是空字符串说明原来的值是null，所以这里还原为null存入数据库
				values.put(SmsField.PERSON, item.getPerson().equals("") ? null : item.getPerson());
				values.put(SmsField.DATE, item.getDate());
				values.put(SmsField.PROTOCOL, item.getProtocol().equals("") ? null : item.getProtocol());
				values.put(SmsField.READ, item.getRead());
				values.put(SmsField.STATUS, item.getStatus());
				values.put(SmsField.TYPE, item.getType());
				values.put(SmsField.REPLY_PATH_PRESENT, item.getReply_path_present().equals("") ? null : item.getReply_path_present());
				values.put(SmsField.BODY, item.getBody());
				values.put(SmsField.LOCKED, item.getLocked());
				values.put(SmsField.ERROR_CODE, item.getError_code());
				values.put(SmsField.SEEN, item.getSeen());
				conResolver.insert(Uri.parse("content://sms"), values);
			}
			cursor.close();
			Toast.makeText(context, "短信恢复成功！", Toast.LENGTH_SHORT).show();
		}
	}

//	public void delete() {
//
//		conResolver.delete(Uri.parse("content://sms"), null, null);
//	}

	public List<SmsItem> getSmsItemsFromXml(){

		SmsItem smsItem = null;
		XmlPullParser xmlPullParser = Xml.newPullParser(); // Create a new xml pull parser.
		
		// Construct the full file path of the backup sms file.
		String absolutePath = Environment.getExternalStorageDirectory() + ConstantVariables.smsBackupLocation
				+ ConstantVariables.smsFile;
		File file = new File(absolutePath);
		if (!file.exists()) {

			Looper.prepare(); // Show a Toast message in the thread.
			Toast.makeText(context, "短信备份文件" + ConstantVariables.smsFile + "不在sd卡中",
					Toast.LENGTH_SHORT).show();
			Looper.loop();//退出线程
//			return null;
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			xmlPullParser.setInput(fis, "UTF-8"); // Set input stream
			int event = xmlPullParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					smsItems = new ArrayList<SmsItem>();
					break;
				case XmlPullParser.START_TAG: // 如果遇到开始标记，如<smsItems>,<smsItem>等
					if ("item".equals(xmlPullParser.getName())) {
						smsItem = new SmsItem();

						smsItem.setAddress(xmlPullParser.getAttributeValue(0));
						smsItem.setPerson(xmlPullParser.getAttributeValue(1));
						smsItem.setDate(xmlPullParser.getAttributeValue(2));
						smsItem.setProtocol(xmlPullParser.getAttributeValue(3));
						smsItem.setRead(xmlPullParser.getAttributeValue(4));
						smsItem.setStatus(xmlPullParser.getAttributeValue(5));
						smsItem.setType(xmlPullParser.getAttributeValue(6));
						smsItem.setReply_path_present(xmlPullParser.getAttributeValue(7));
						smsItem.setBody(xmlPullParser.getAttributeValue(8));
						smsItem.setLocked(xmlPullParser.getAttributeValue(9));
						smsItem.setError_code(xmlPullParser.getAttributeValue(10));
						smsItem.setSeen(xmlPullParser.getAttributeValue(11));
					}
					break;
				case XmlPullParser.END_TAG:// 结束标记,如</smsItems>,</smsItem>等
					if ("item".equals(xmlPullParser.getName())) {
						smsItems.add(smsItem);
						smsItem = null;
					}
					break;
				}
				event = xmlPullParser.next();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Looper.prepare();
			Toast.makeText(context, "文件未找到，短信恢复出错",
					Toast.LENGTH_SHORT).show();
			Looper.loop();
			e.printStackTrace();
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			Looper.prepare();
			Toast.makeText(context, "文件解析出错，短信恢复出错",
					Toast.LENGTH_SHORT).show();
			Looper.loop();
			e.printStackTrace();		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Looper.prepare();
			Toast.makeText(context, "文件IO异常，短信恢复出错",
					Toast.LENGTH_SHORT).show();
			Looper.loop();
			e.printStackTrace();
		}
		return smsItems;
	}
}
