package com.utar.merchant.nfcServices;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.utar.merchant.R;
import com.utar.merchant.ui.settings.AccountExportActivity;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class AccountExportHCEService extends HostApduService {

    private final String TAG = "AccountExportHCEService";
    private final String MERCHANT_LOGIN_AID = "F888222444";

    private final byte[] CORRECT_RESPOND_APP = {(byte) 0x90, (byte) 0x00};
    private static final byte[] UNKNOWN_CMD_SW = {(byte) 0x00, (byte) 0x00};
    private final byte[] REQUEST_INFO = {(byte)0x91, (byte)0x92};

    public interface ViewCallback{
        public void setAnimation(int rawRes, boolean repeat);
        void setText(int rawRes);
    }

    private WeakReference<ViewCallback> mViewCallback;

    public AccountExportHCEService(){}
    public  AccountExportHCEService(ViewCallback viewCallback){
        mViewCallback =  new WeakReference<ViewCallback>(viewCallback);
    }

    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        Log.i(TAG, "Received APDU: " + Nfc.ByteArrayToHexString(bytes));

        if (Arrays.equals(Nfc.BuildSelectApdu(MERCHANT_LOGIN_AID), bytes)) {
            if(mViewCallback != null)
                mViewCallback.get().setAnimation(R.raw.nfc_processing, true);
            return CORRECT_RESPOND_APP;
        }

        else if(Arrays.equals(bytes, REQUEST_INFO)) {

            AccountExportActivity.getInstance().finish();
            byte uid[] = FirebaseAuth.getInstance().getUid().getBytes();
            FirebaseAuth.getInstance().signOut();

            if(mViewCallback != null)
                mViewCallback.get().setAnimation(R.raw.nfc_finish, false);
            return uid;
        }
        return UNKNOWN_CMD_SW;
    }

    @Override
    public void onDeactivated(int i) {

    }


}
