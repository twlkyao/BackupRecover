package com.twlkyao.backuprecover;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	// Buttons to operate Sms.
	private Button backupSmsButton;
	private Button recoverSmsButton;
	
	// Buttons to operate Contacts.
	private Button backupContactsButton;
	private Button recoverContactsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		setListeners();
	}

	/**
	 * Find the Views on the layout file.
	 */
	public void findViews() {
		backupSmsButton = (Button) this.findViewById(R.id.backupSms);
		recoverSmsButton = (Button) this.findViewById(R.id.recoverSms);
		
		backupContactsButton = (Button) this.findViewById(R.id.backupContacts);
		recoverContactsButton = (Button) this.findViewById(R.id.recoverContacts);
	}
	
	/**
	 * Set listeners for the Buttons.
	 */
	public void setListeners() {
		
		/***********************************************************************
		 * Sms operation.
		 **********************************************************************/
		/**
		 * Backup Sms Button Listener.
		 */
		backupSmsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExportSms exportSmsXml = new ExportSms(getApplicationContext());
				try {
					exportSmsXml.createXml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		/**
		 * Recover Sms Button Listener.
		 */
		recoverSmsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImportSms importSms = new ImportSms(getApplicationContext());
				importSms.InsertSMS();
			}
		});
		
		/***********************************************************************
		 * Contacts operation.
		 **********************************************************************/
		/**
		 * Backup Contacts Button Listener.
		 */
		backupContactsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExportContacts exportContactsXml = new ExportContacts(getApplicationContext());
				try {
					exportContactsXml.createXml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		/**
		 * Recover Contacts Button Listener.
		 */
		recoverContactsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImportContacts importContacts= new ImportContacts(getApplicationContext());
				importContacts.InsertContacts();
			}
		});
	}
}
