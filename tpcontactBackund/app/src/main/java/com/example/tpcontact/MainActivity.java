package com.example.tpcontact;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 102;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 103;

    private ListView lvContacts;
    private ArrayList<Contact> contactsList = new ArrayList<>();
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvContacts = findViewById(R.id.lvContacts);

        // Request contacts permission first
        requestContactsPermission();

        // Set up click listener for contacts
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = contactsList.get(position);
                showContactOptions(selectedContact);
            }
        });

        // Request phone state permission and set up listener only if permission is granted
        requestPhoneStatePermission();
    }

    private void requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            setupPhoneStateListener();
        }
    }

    private void setupPhoneStateListener() {
        try {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                manager.listen(new TelListener(), PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            Log.e("TP6", "Error setting up phone state listener: " + e.getMessage());
        }
    }

    private void requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts();
                } else {
                    Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, call the contact
                    if (pendingCallContact != null) {
                        callContact(pendingCallContact);
                        pendingCallContact = null;
                    }
                } else {
                    Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, send SMS
                    if (pendingSmsContact != null) {
                        sendSms(pendingSmsContact);
                        pendingSmsContact = null;
                    }
                } else {
                    Toast.makeText(this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupPhoneStateListener();
                } else {
                    Toast.makeText(this, "Permission denied to read phone state", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Contact pendingCallContact = null;
    private Contact pendingSmsContact = null;

    private void loadContacts() {
        contactsList.clear();
        ContentResolver cr = getContentResolver();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Cursor phones = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (phones != null) {
            int nameIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (phones.moveToNext()) {
                String name = nameIndex != -1 ? phones.getString(nameIndex) : "Unknown";
                String phoneNumber = numberIndex != -1 ? phones.getString(numberIndex) : "";

                contactsList.add(new Contact(name, phoneNumber));
            }
            phones.close();
        }

        // Create and set adapter
        adapter = new ContactAdapter(this, contactsList);
        lvContacts.setAdapter(adapter);
    }

    private void showContactOptions(final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(contact.getName())
                .setItems(new String[]{"Call", "Send SMS"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Call
                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                pendingCallContact = contact;
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        PERMISSIONS_REQUEST_CALL_PHONE);
                            } else {
                                callContact(contact);
                            }
                            break;
                        case 1: // SMS
                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                pendingSmsContact = contact;
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.SEND_SMS},
                                        PERMISSIONS_REQUEST_SEND_SMS);
                            } else {
                                sendSms(contact);
                            }
                            break;
                    }
                });
        builder.show();
    }

    private void callContact(Contact contact) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
        startActivity(callIntent);
    }

    private void sendSms(Contact contact) {
        // Show dialog to choose between SMS app or direct send
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send SMS")
                .setItems(new String[]{"Use SMS App", "Send Direct SMS"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // SMS App
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                            smsIntent.setData(Uri.parse("smsto:" + contact.getPhoneNumber()));
                            smsIntent.putExtra("sms_body", "Hello from TP6 App!");
                            startActivity(smsIntent);
                            break;
                        case 1: // Direct SMS
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(
                                        contact.getPhoneNumber(),
                                        null,
                                        "Hello from TP6 App!",
                                        null,
                                        null);
                                Toast.makeText(MainActivity.this,
                                        "SMS sent successfully", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this,
                                        "SMS failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            break;
                    }
                });
        builder.show();
    }

    // Phone state listener class
    class TelListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i("TP6", "RINGING, number: " + phoneNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("TP6", "OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("TP6", "IDLE");
                    break;
            }
            super.onCallStateChanged(state, phoneNumber);
        }
    }
}