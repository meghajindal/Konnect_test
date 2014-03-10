/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.konnect_test;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Android Google+ Quickstart activity.
 * 
 * Demonstrates Google+ Sign-In and usage of the Google+ APIs to retrieve a
 * users profile information.
 */
public class MainActivity extends FragmentActivity implements
    ConnectionCallbacks, OnConnectionFailedListener,
     View.OnClickListener {

  private static final String TAG = "Konnect";

  private static final int STATE_DEFAULT = 0;
  private static final int STATE_SIGN_IN = 1;
  private static final int STATE_IN_PROGRESS = 2;

  private static final int RC_SIGN_IN = 0;

  private static final int DIALOG_PLAY_SERVICES_ERROR = 0;
  private boolean mIntentInProgress;
  private static final String SAVED_PROGRESS = "sign_in_progress";
  
  // GoogleApiClient wraps our service connection to Google Play services and
  // provides access to the users sign in state and Google's APIs.
  private GoogleApiClient mGoogleApiClient;
  
  // We use mSignInProgress to track whether user has clicked sign in.
  // mSignInProgress can be one of three values:
  //
  //       STATE_DEFAULT: The default state of the application before the user
  //                      has clicked 'sign in', or after they have clicked
  //                      'sign out'.  In this state we will not attempt to
  //                      resolve sign in errors and so will display our
  //                      Activity in a signed out state.
  //       STATE_SIGN_IN: This state indicates that the user has clicked 'sign
  //                      in', so resolve successive errors preventing sign in
  //                      until the user has successfully authorized an account
  //                      for our app.
  //   STATE_IN_PROGRESS: This state indicates that we have started an intent to
  //                      resolve an error, and so we should not start further
  //                      intents until the current intent completes.
  private int mSignInProgress;
  
  // Used to store the PendingIntent most recently returned by Google Play
  // services until the user clicks 'sign in'.
  private PendingIntent mSignInIntent;
  
  // Used to store the error code most recently returned by Google Play services
  // until the user clicks 'sign in'.
  private int mSignInError;
  private boolean mSignInClicked;
  private SignInButton mSignInButton;
  private Button mSignOutButton;
  private TextView mStatus;
  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
    mSignOutButton = (Button) findViewById(R.id.sign_out_button);
   
    //Toast.makeText(this, "I am trying", Toast.LENGTH_LONG).show();
    mSignInButton.setOnClickListener(this);
    mSignOutButton.setOnClickListener(this);
    
    if (savedInstanceState != null) {
      mSignInProgress = savedInstanceState
          .getInt(SAVED_PROGRESS, STATE_DEFAULT);
    }
    mGoogleApiClient = buildGoogleApiClient();
   
  }
  
  private GoogleApiClient buildGoogleApiClient() {
    // When we build the GoogleApiClient we specify where connected and
    // connection failed callbacks should be returned, which Google APIs our
    // app uses and which OAuth 2.0 scopes our app requests.
    return new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API, null)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
  }

  @Override
  protected void onStart() {
    super.onStart();
 //   Toast.makeText(this, "I am double trying", Toast.LENGTH_LONG).show();
    mGoogleApiClient.connect();
    Toast.makeText(this, "client connected", Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(SAVED_PROGRESS, mSignInProgress);
  }

  @Override
  public void onClick(View v) {
	  
	// Toast.makeText(this, "abc", Toast.LENGTH_LONG).show();
	
	  if ( !mGoogleApiClient.isConnecting()) {
			  
	  switch (v.getId()) {
      case R.id.sign_in_button:
    	  mSignInClicked = true;
       
        resolveSignInError();
        break;
      case R.id.sign_out_button:
        // We clear the default account on sign out so that Google Play
        // services will not return an onConnected callback without user
        // interaction.
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        mGoogleApiClient.disconnect();
        mGoogleApiClient.connect();
        break;
	  }
	  }
	  
  }
  
  /* onConnected is called when our Activity successfully connects to Google
   * Play services.  onConnected indicates that an account was selected on the
   * device, that the selected account has granted any requested permissions to
   * our app and that we were able to establish a service connection to Google
   * Play services.
   */
  @Override
  public void onConnected(Bundle connectionHint) {
    // Reaching onConnected means we consider the user signed in.
    Log.i(TAG, "onConnected");
 //   Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    // Update the user interface to reflect that the user is signed in.
    mSignInButton.setEnabled(false);
    mSignOutButton.setEnabled(true);
    
    // Retrieve some profile information to personalize our app for the user.
    getProfileInformation();
   // Toast.makeText(this, currentUser.getDisplayName(), Toast.LENGTH_LONG).show();
  //  mStatus.setText(String.format(
      //  getResources().getString(R.string.signed_in_as),
      //  currentUser.getDisplayName()));

   // Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
     //   .setResultCallback(this);
    
    // Indicate that the sign in process is complete.
    mSignInProgress = STATE_DEFAULT;
  }

  /* onConnectionFailed is called when our Activity could not connect to Google
   * Play services.  onConnectionFailed indicates that the user needs to select
   * an account, grant permissions or resolve an error in order to sign in.
   */
  @Override
  public void onConnectionFailed(ConnectionResult result) {
    // Refer to the javadoc for ConnectionResult to see what error codes might
    // be returned in onConnectionFailed.
    Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
        + result.getErrorCode());
   // Toast.makeText(this, "User", Toast.LENGTH_LONG).show();
    if (mSignInProgress != STATE_IN_PROGRESS) {
      // We do not have an intent in progress so we should store the latest
      // error resolution intent for use when the sign in button is clicked.
      mSignInIntent = result.getResolution();
      mSignInError = result.getErrorCode();
      
      if (mSignInProgress == STATE_SIGN_IN) {
        // STATE_SIGN_IN indicates the user already clicked the sign in button
        // so we should continue processing errors until the user is signed in
        // or they click cancel.
        resolveSignInError();
      }
     
    }
    onSignedOut();
    // In this sample we consider the user signed out whenever they do not have
    // a connection to Google Play services.
   // onSignedOut();
  }
  
  /* Starts an appropriate intent or dialog for user interaction to resolve
   * the current error preventing the user from being signed in.  This could
   * be a dialog allowing the user to select an account, an activity allowing
   * the user to consent to the permissions being requested by your app, a
   * setting to enable device networking, etc.
   */
  private void resolveSignInError() {
	//  Toast.makeText(this, "resolve sign in err ", Toast.LENGTH_LONG).show();
    if (mSignInIntent != null) {
      // We have an intent which will allow our user to sign in or
      // resolve an error.  For example if the user needs to
      // select an account to sign in with, or if they need to consent
      // to the permissions your app is requesting.

      try {
        // Send the pending intent that we stored on the most recent
        // OnConnectionFailed callback.  This will allow the user to
        // resolve the error currently preventing our connection to
        // Google Play services.  
        mSignInProgress = STATE_IN_PROGRESS;
        startIntentSenderForResult(mSignInIntent.getIntentSender(),
            RC_SIGN_IN, null, 0, 0, 0);
      } catch (SendIntentException e) {
        Log.i(TAG, "Sign in intent could not be sent: "
            + e.getLocalizedMessage());
        // The intent was canceled before it was sent.  Attempt to connect to
        // get an updated ConnectionResult.
        mSignInProgress = STATE_SIGN_IN;
        Toast.makeText(this, "reconnect", Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
      }
    } else {
      // Google Play services wasn't able to provide an intent for some
      // error types, so we show the default Google Play services error
      // dialog which may still start an intent on our behalf if the
      // user can resolve the issue.
      showDialog(DIALOG_PLAY_SERVICES_ERROR);
    }  
  }
  
  @Override
  /* protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    switch (requestCode) {
      case RC_SIGN_IN:
        if (resultCode == RESULT_OK) {
          // If the error resolution was successful we should continue
          // processing errors.
          mSignInProgress = STATE_SIGN_IN;
        } else {
          // If the error resolution was not successful or the user canceled,
          // we should stop processing errors.
          mSignInProgress = STATE_DEFAULT;
        }
        
        if (!mGoogleApiClient.isConnecting()) {
          // If Google Play services resolved the issue with a dialog then
          // onStart is not called so we need to re-attempt connection here.
          mGoogleApiClient.connect();
        }
        break;
    }
  }
    */
  protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
	  if (requestCode == RC_SIGN_IN) {
	    if (responseCode != RESULT_OK) {
	      mSignInClicked = false;
	    }

	    mIntentInProgress = false;

	    if (!mGoogleApiClient.isConnecting()) {
	      mGoogleApiClient.connect();
	    }
	  }
	}
 
  private void onSignedOut() {
	    // Update the UI to reflect that the user is signed out.
	    mSignInButton.setEnabled(true);
	    mSignOutButton.setEnabled(false);
	    
	  }
 

  @Override
  public void onConnectionSuspended(int cause) {
    // The connection to Google Play services was lost for some reason.
    // We call connect() to attempt to re-establish the connection or get a
    // ConnectionResult that we can attempt to resolve.
    mGoogleApiClient.connect();
  }

  /**
   * Fetching user's information name, email, profile pic
   * */
  private void getProfileInformation() {
      try {
          if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
        	  Toast.makeText(this, "inside getCurrentPerson", Toast.LENGTH_LONG).show();
              Person currentPerson = Plus.PeopleApi
                      .getCurrentPerson(mGoogleApiClient);
              String personName = currentPerson.getDisplayName();
              String personPhotoUrl = currentPerson.getImage().getUrl();
              String personGooglePlusProfile = currentPerson.getUrl();
              String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
   
              Log.e(TAG, "Name: " + personName + ", plusProfile: "
                      + personGooglePlusProfile + ", email: " + email
                      + ", Image: " + personPhotoUrl);
   
             Toast.makeText(this, "Userme "+ personName, Toast.LENGTH_LONG).show();
             Intent intent = new Intent(this, abc.class);
             intent.putExtra("personName", personName);

             this.startActivity(intent);
   
          } else {
              Toast.makeText(getApplicationContext(),
                      "Person information is null", Toast.LENGTH_LONG).show();
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

 
}
