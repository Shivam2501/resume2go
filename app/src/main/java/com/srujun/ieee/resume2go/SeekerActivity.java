package com.srujun.ieee.resume2go;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;


public class SeekerActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.srujun.ieee.resume2go.MESSAGE";

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver;

    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, getApplicationContext());

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    public void sendMessage(View view) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 2);
    }

    public void discoverPeers(View view) {
        manager.addServiceRequest(channel, WifiP2pUpnpServiceRequest.newInstance(), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {
                Toast toast = Toast.makeText(getApplicationContext(), "Could not connect to DnsSd peer.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        manager.setUpnpServiceResponseListener(channel, new WifiP2pManager.UpnpServiceResponseListener() {
            @Override
            public void onUpnpServiceAvailable(List<String> strings, final WifiP2pDevice wifiP2pDevice) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = wifiP2pDevice.deviceAddress;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        TextView connectedToTextView = (TextView) findViewById(R.id.textview_connected_device);
                        connectedToTextView.setText("Connected to: " + wifiP2pDevice.deviceName);
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast toast = Toast.makeText(getApplicationContext(), "DnsSd service not found.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });

        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getApplicationContext(), "Searching started.", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast toast = Toast.makeText(getApplicationContext(), "Could not start search.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void broadcastService(View view) {
        WifiP2pServiceInfo serviceInfo = WifiP2pUpnpServiceInfo.newInstance(UUID.randomUUID().toString(), "TESTO", null);
        manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getApplicationContext(), "Broadcast started.", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(int i) {
                Toast toast = Toast.makeText(getApplicationContext(), "Broadcast failed.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        if(resultcode == RESULT_OK) {
            if(requestcode == 2) {
                Uri selectedImageUri = data.getData();
                Cursor fileCursor = getContentResolver().query(selectedImageUri, null, null, null, null);

                TextView fileNameLabel = (TextView) findViewById(R.id.file_name_textview);
                int nameIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileCursor.moveToFirst();
                fileNameLabel.setText(fileCursor.getString(nameIndex));
                fileCursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seeker, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
