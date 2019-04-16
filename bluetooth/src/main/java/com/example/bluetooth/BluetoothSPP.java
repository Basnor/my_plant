package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

@SuppressLint("NewApi")
public class BluetoothSPP {
    // Listener for Bluetooth Status & Connection
    private BluetoothStateListener mBluetoothStateListener = null;
    private OnDataReceivedListener mDataReceivedListener = null;

    // Context from activity which call this class
    private Context mContext;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;

    // Member object for the chat services
    private BluetoothService mChatService = null;

    // Name and Address of the connected device
    private String mDeviceName = null;
    private String mDeviceAddress = null;

    private boolean isConnected = false;
    private boolean isConnecting = false;

    private boolean isAndroid = BluetoothState.DEVICE_ANDROID;

    public BluetoothSPP(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public interface BluetoothStateListener {
        void onServiceStateChanged(int state);
    }

    public interface OnDataReceivedListener {
        void onDataReceived(byte[] data, String message);
    }

    public boolean isBluetoothAvailable() {
        try {
            if (mBluetoothAdapter == null || mBluetoothAdapter.getAddress().equals(null))
                return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isServiceAvailable() {
        return mChatService != null;
    }

    public void setupService() {
        mChatService = new BluetoothService(mContext, mHandler);
    }

    public int getServiceState() {
        if(mChatService != null)
            return mChatService.getState();
        else
            return -1;
    }

    public void startService(boolean isAndroid) {
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothState.STATE_NONE) {
                mChatService.start(isAndroid);
                BluetoothSPP.this.isAndroid = isAndroid;
            }
        }
    }

    public void stopService() {
        if (mChatService != null) {
            mChatService.stop();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mChatService != null) {
                    mChatService.stop();
                }
            }
        }, 500);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothState.MESSAGE_WRITE:
                    break;
                case BluetoothState.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf);
                    if(readBuf != null && readBuf.length > 0) {
                        if(mDataReceivedListener != null)
                            mDataReceivedListener.onDataReceived(readBuf, readMessage);
                    }
                    break;
                case BluetoothState.MESSAGE_DEVICE_NAME:
                    mDeviceName = msg.getData().getString(BluetoothState.DEVICE_NAME);
                    mDeviceAddress = msg.getData().getString(BluetoothState.DEVICE_ADDRESS);
                    isConnected = true;
                    break;
                case BluetoothState.MESSAGE_TOAST:
                    Toast.makeText(mContext, msg.getData().getString(BluetoothState.TOAST)
                            , Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothState.MESSAGE_STATE_CHANGE:
                    if(mBluetoothStateListener != null)
                        mBluetoothStateListener.onServiceStateChanged(msg.arg1);
                    if(isConnected && msg.arg1 != BluetoothState.STATE_CONNECTED) {

                        isConnected = false;
                        mDeviceName = null;
                        mDeviceAddress = null;
                    }

                    if(!isConnecting && msg.arg1 == BluetoothState.STATE_CONNECTING) {
                        isConnecting = true;
                    } else if(isConnecting) {
                        isConnecting = false;
                    }
                    break;
            }
        }
    };

    public void connect(Intent data) {
        String address = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device);
    }

    public void connect(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device);
    }

    public void disconnect() {
        if(mChatService != null) {
            mChatService.stop();
            if(mChatService.getState() == BluetoothState.STATE_NONE) {
                mChatService.start(BluetoothSPP.this.isAndroid);
            }
        }
    }

    public void setBluetoothStateListener (BluetoothStateListener listener) {
        mBluetoothStateListener = listener;
    }

    public void setOnDataReceivedListener (OnDataReceivedListener listener) {
        mDataReceivedListener = listener;
    }

    public void enable() {
        mBluetoothAdapter.enable();
    }

    public void send(byte[] data, boolean CRLF) {
        if(mChatService.getState() == BluetoothState.STATE_CONNECTED) {
            if(CRLF) {
                byte[] data2 = new byte[data.length + 2];
                for(int i = 0 ; i < data.length ; i++)
                    data2[i] = data[i];
                data2[data2.length - 2] = 0x0A;
                data2[data2.length - 1] = 0x0D;
                mChatService.write(data2);
            } else {
                mChatService.write(data);
            }
        }
    }

    public void send(String data, boolean CRLF) {
        if(mChatService.getState() == BluetoothState.STATE_CONNECTED) {
            if(CRLF)
                data += "\r\n";
            mChatService.write(data.getBytes());
        }
    }

    public String getConnectedDeviceName() {
        return mDeviceName;
    }

    public String getConnectedDeviceAddress() {
        return mDeviceAddress;
    }

}
