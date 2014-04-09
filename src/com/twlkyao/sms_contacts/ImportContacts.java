package com.twlkyao.sms_contacts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.twlkyao.utils.ConstVariables;
import com.twlkyao.utils.ContactsField;
import com.twlkyao.utils.ContactsItem;
import com.twlkyao.utils.LogUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;

public class ImportContacts {

	private boolean debug = true;
	private String tag = "ImportContacts";
	private LogUtils logUtils = new LogUtils(debug, tag);
	public static final String CONTACTS_URI_ALL = "android.provider.ContactsContract.Contacts.CONTENT_URI"; // Contacts uri.
	private Context context;
	private List<ContactsItem> contactsItems;
	private ContentResolver conResolver;
	
	public ImportContacts(Context context) {
		this.context = context;
		conResolver = context.getContentResolver();
	}

	public void InsertContacts() {
		/**
		 * 放一个解析xml文件的模块
		 */
		contactsItems = this.getContactsItemsFromXml();
		
		logUtils.d(tag, "InsertContacts");
		
		for (ContactsItem item : contactsItems) {

			Uri uri = Uri.parse(CONTACTS_URI_ALL);
			// 判断短信数据库中是否已包含该条短信，如果有，则不需要恢复
			Cursor cursor = conResolver.query(uri, new String[] { ContactsField.DISPLAY_NAME },
					ContactsField.DISPLAY_NAME + "=?",
					new String[] { item.getDisplay_name() }, null);

			if (!cursor.moveToFirst()) {// 没有该条短信
				
				ContentValues values = new ContentValues();
				values.put(ContactsField.DISPLAY_NAME, item.getDisplay_name());
				values.put(ContactsField.HAS_PHONE_NUMBER, item.getHas_phone_number());
				values.put(ContactsField.PHONE_NUMBER, item.getPhone_Number());
				conResolver.insert(uri, values);
			}
			cursor.close();
			Toast.makeText(context, "通讯录恢复成功！", Toast.LENGTH_SHORT).show();
		}
	}

	public List<ContactsItem> getContactsItemsFromXml(){

		ContactsItem contactsItem = null;
		XmlPullParser xmlPullParser = Xml.newPullParser(); // Create a new xml pull parser.
		
		// Construct the full file path of the backup contacts file.
		String absolutePath = Environment.getExternalStorageDirectory() + ConstVariables.smsBackupLocation + ConstVariables.smsFile;
		File file = new File(absolutePath);
		if (!file.exists()) {

			Looper.prepare(); // Show a Toast message in the thread.
			Toast.makeText(context, "通讯录备份文件" + ConstVariables.contactsFile + "不在sd卡中",
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
					contactsItems = new ArrayList<ContactsItem>();
					break;
				case XmlPullParser.START_TAG: // 如果遇到开始标记，如<contactsItems>,<contactsItem>等
					if ("item".equals(xmlPullParser.getName())) {
						contactsItem = new ContactsItem();

						contactsItem.setDisplay_name(xmlPullParser.getAttributeName(0));
						contactsItem.setHas_phone_number(xmlPullParser.getAttributeName(1));
						contactsItem.setPhone_Number(xmlPullParser.getAttributeName(2));
					}
					break;
				case XmlPullParser.END_TAG:// 结束标记,如</contactsItems>,</contactsItem>等
					if ("item".equals(xmlPullParser.getName())) {
						contactsItems.add(contactsItem);
						contactsItem = null;
					}
					break;
				}
				event = xmlPullParser.next();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Looper.prepare();
			Toast.makeText(context, "文件未找到，通讯录恢复出错",
					Toast.LENGTH_SHORT).show();
			Looper.loop();
			e.printStackTrace();
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			Looper.prepare();
			Toast.makeText(context, "文件解析出错，通讯录恢复出错",
					Toast.LENGTH_SHORT).show();
			Looper.loop();
			e.printStackTrace();		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Looper.prepare();
			Toast.makeText(context, "文件IO异常，通讯录恢复出错",
					Toast.LENGTH_SHORT).show();
			Looper.loop();
			e.printStackTrace();
		}
		return contactsItems;
	}
}
