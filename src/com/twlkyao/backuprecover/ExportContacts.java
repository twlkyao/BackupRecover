package com.twlkyao.backuprecover;

import java.io.File;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlSerializer;

import com.twlkyao.utils.ConstantVariables;
import com.twlkyao.utils.ContactsField;
import com.twlkyao.utils.LogUtils;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

public class ExportContacts {
	
	
	private boolean debug = true;
	private String tag = "ExportContacts";
	private LogUtils logUtils = new LogUtils(debug, tag);
	
	Context context;
	public static final String CONTACTS_URI_ALL = "content://com.android.contacts/data/phones"; // Contacts uri.
	private FileOutputStream outStream = null;
	private XmlSerializer serializer; // XmlSerializer.

	public ExportContacts(Context context) {
		this.context = context;
	}

	/**
	 * Set the parameters of the xml backup file.
	 */
	public void xmlStart() {

		// Construct the file path to store the sms xml file.
		String path = Environment.getExternalStorageDirectory().getPath() + ConstantVariables.contactsBackupLocation;
		
		logUtils.d(tag, "path:" + path);

		// Create the directory to store the sms xml file.
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		// Create the sms xml file.
		File file = new File(path, "contacts.xml");
		try {
			outStream = new FileOutputStream(file);
			serializer = Xml.newSerializer();
			serializer.setOutput(outStream, "UTF-8"); // Set the encode to UTF-8.
			serializer.startDocument("UTF-8", true); // Write <?xml declaration.
			serializer.startTag(null, "contacts"); // Write a start tag.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the xml file.
	 * @return True, if the backup is succeeded.
	 * @throws Exception
	 */
	public boolean createXml() throws Exception {

		this.xmlStart(); // Set the parameters of the xml file.
		Cursor cursor = null;
		try {
			ContentResolver conResolver = context.getContentResolver();
			
			
			String[] projection = new String[] { ContactsField.DISPLAY_NAME,
					ContactsField.HAS_PHONE_NUMBER, ContactsField.PHONE_NUMBER };
			
			Uri uri = Uri.parse(CONTACTS_URI_ALL);
			
			cursor = conResolver.query(uri, projection, null, null, "_id asc");
			if (cursor.moveToFirst()) {
				
				String display_name;
				String has_phone_number;
				String phone_number;
				do {
					
					display_name = cursor.getString(cursor.getColumnIndex(ContactsField.DISPLAY_NAME));
					if(display_name == null) {
						display_name = "";
					}
					
					has_phone_number = cursor.getString(cursor.getColumnIndex(ContactsField.HAS_PHONE_NUMBER));
					if(has_phone_number == null) {
						has_phone_number = "";
					}
					
					phone_number = cursor.getString(cursor.getColumnIndex(ContactsField.PHONE_NUMBER));
					if(phone_number == null) {
						phone_number = "";
					}
					// xml subtag.
					// Start tag.
					serializer.startTag(null, "item");
					// Add attribute.
					serializer.attribute(null, ContactsField.DISPLAY_NAME, display_name);
					serializer.attribute(null, ContactsField.HAS_PHONE_NUMBER, has_phone_number);
					serializer.attribute(null, ContactsField.PHONE_NUMBER, phone_number);
					// End tag.
					serializer.endTag(null, "item");

				} while (cursor.moveToNext());
			} else {
				return false;
			}
		} catch (SQLiteException ex) {
			ex.printStackTrace();
			
			logUtils.d(tag, "SQLitException:" + ex.getMessage());
			
		}finally {
			if(cursor != null) {
				cursor.close(); // Close cursor.
			}
		}
		serializer.endTag(null, "contacts");
		serializer.endDocument();
		outStream.flush();
		outStream.close();
		Toast.makeText(context, "通讯录备份成功！", Toast.LENGTH_SHORT).show();
		return true;
	}
}