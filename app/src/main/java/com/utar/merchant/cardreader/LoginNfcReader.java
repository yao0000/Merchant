package com.utar.merchant.cardreader;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Arrays;

public class LoginNfcReader implements NfcAdapter.ReaderCallback{

    private final String TAG = "LoginNfcReader";
    private final String MERCHANT_LOGIN_AID = "F888222444";

    private final byte[] CORRECT_RESPOND_APP = {(byte)0x90, (byte)0x00};
    private final byte[] UNKNOWN_CMD_SW = {(byte)0x00, (byte)0x00};


    public interface LoginCallback{

    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag is discovered");
        if(tag == null){
            Log.i(TAG, "tag is null object reference");
            return;
        }
        IsoDep isoDep = IsoDep.get(tag);

        if(isoDep == null){
            return;
        }

        try {
            isoDep.connect();

            if(!isoDep.isConnected()){
                throw new IOException("Connection fail");
            }

            byte[] apduCmd = Nfc.BuildSelectApdu(MERCHANT_LOGIN_AID);
            Log.i(TAG, "Requesting AID: " + HCECardReader.ByteArrayToHexString(apduCmd));

            byte[] result = isoDep.transceive(apduCmd);
            int resultLength = result.length;
            byte[] statusWord = {result[resultLength-2], result[resultLength-1]};
            byte[] payload = Arrays.copyOf(result, result.length - 2);

            if(Arrays.equals(CORRECT_RESPOND_APP, statusWord)){

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

}
