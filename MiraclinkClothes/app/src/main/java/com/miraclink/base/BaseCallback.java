package com.miraclink.base;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BaseCallback {
    void onDeviceChange( BluetoothGattCharacteristic bluetoothGattCharacteristic);
    void onDeviceWrite(boolean status,BluetoothGattCharacteristic bluetoothGattCharacteristic);

}
