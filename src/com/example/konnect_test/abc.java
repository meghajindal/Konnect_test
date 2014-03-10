package com.example.konnect_test;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

public class abc extends Activity implements View.OnClickListener {
	 private String CompanyName,JobTitle,Email,Phone,Name;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.abc);
	        EditText    Text1 = (EditText) findViewById(R.id.editText1);   
	        EditText    Text2 = (EditText) findViewById(R.id.editText2); 
	        EditText    Text3 = (EditText) findViewById(R.id.editText3); 
	        EditText    Text4 = (EditText) findViewById(R.id.editText4); 
	        EditText    Text5 = (EditText) findViewById(R.id.editText5); 
	        Button btn = (Button) findViewById(R.id.button1);
	        Bundle extras = getIntent().getExtras();
	        if (extras != null) {
	            String value = extras.getString("personName");
	            Text1.setText(value);
	            Text1.setEnabled(false);
	        }
	       
	        btn.setOnClickListener(this); 
	    }
	    public void onClick(View v) {
	    	if (v ==findViewById(R.id.editText5))
	    		 Phone = v.toString();
	    	if (v ==findViewById(R.id.editText4))
	    		 Email = v.toString();
	    	if (v ==findViewById(R.id.editText3))
	    		 JobTitle = v.toString();
	    	if (v ==findViewById(R.id.editText2))
	    		 CompanyName = v.toString();
	    	if (v ==findViewById(R.id.editText1))
	    		 Name = v.toString();
	    	 
		        
	        Toast.makeText(this, "comapny name"+v.getId(), Toast.LENGTH_LONG).show();
	      ArrayList<ContentProviderOperation> ops = 
	                new ArrayList<ContentProviderOperation>();

	        ops.add(ContentProviderOperation.newInsert(
	                ContactsContract.RawContacts.CONTENT_URI)
	                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
	                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
	                .build()
	            );

	            //------------------------------------------------------ Names
	           if(Name != null)
	            {           
	                ops.add(ContentProviderOperation.newInsert(
	                    ContactsContract.Data.CONTENT_URI)              
	                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                    .withValue(ContactsContract.Data.MIMETYPE,
	                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
	                    .withValue(
	                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,     
	                        Name).build()
	                );
	            } 

	            //------------------------------------------------------ Mobile Number                      
	            if(Phone != null)
	            {
	                ops.add(ContentProviderOperation.
	                    newInsert(ContactsContract.Data.CONTENT_URI)
	                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                    .withValue(ContactsContract.Data.MIMETYPE,
	                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
	                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Phone)
	                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
	                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
	                    .build()
	                );
	            }

	                               
	                            

	                                //------------------------------------------------------ Email
	                                if(Email != null)
	                                {
	                                     ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	                                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                                                .withValue(ContactsContract.Data.MIMETYPE,
	                                                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
	                                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, Email)
	                                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
	                                                .build());
	                                }

	                                //------------------------------------------------------ Organization
	                                if(!CompanyName.equals("") && !JobTitle.equals(""))
	                                {
	                                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	                                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                                            .withValue(ContactsContract.Data.MIMETYPE,
	                                                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
	                                            .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, CompanyName)
	                                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
	                                            .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, JobTitle)
	                                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
	                                            .build());
	                                }

	                                // Asking the Contact provider to create a new contact                  
	                                try 
	                                {
	                                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
	                                } 
	                                catch (Exception e) 
	                                {               
	                                    e.printStackTrace();
	                                  //  Toast.makeText(myContext, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
	                                } 

	    
	    }

	    
}
