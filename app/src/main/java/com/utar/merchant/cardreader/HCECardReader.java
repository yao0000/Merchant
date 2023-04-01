package com.utar.merchant.cardreader;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import java.lang.ref.WeakReference;

public class HCECardReader implements NfcAdapter.ReaderCallback {

    // Format: [Class | Instruction | Parameter 1 | Parameter 2]

    private static final String TAG = "E-wallet Card Reader";

    //AID for card services
    private static final String WALLET_CARD_AID = "F222266688";

    // ISO-DEP command HEADER for selecting an AID.
    // Define the caller header

    // -> Select the corresponding Application ID
    private static final String SELECT_APDU_HEADER = "00A40400";
    // -> To get data
    private static final String GET_DATA_APDU_HEADER = "00CA0000";

    // -> To return status
    private static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};

    // Weak reference to prevent retain loop. mAccountCallback is responsible for exiting
    // foreground mode before it becomes invalid (e.g. during onPause() or onStop()).
    private WeakReference<AccountCallback> mAccountCallback;

    public interface AccountCallback {
        public void onAccountReceived(String account);
    }

    public HCECardReader(AccountCallback accountCallback) {
        mAccountCallback = new WeakReference<AccountCallback>(accountCallback);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered");

        IsoDep isoDep = IsoDep.get(tag);

        if(tag == null){
            Log.e(TAG, "Tag is null object reference");
            return;
        }
        try{
            //connect to hce device
            isoDep.connect();
            Log.i(TAG, "Timeout: " + isoDep.getTimeout());
            isoDep.setTimeout(5000);
            Log.i(TAG, "Timeout: " + isoDep.getTimeout());
            Log.i(TAG, "Max Transceive Length: " + isoDep.getMaxTransceiveLength());
            Log.i(TAG, "Requesting Remote AID: " + WALLET_CARD_AID);

            byte[] apduCommand = BuildSelectApdu(WALLET_CARD_AID);

            Log.i(TAG, "Connect to corresponding AID by using command: " + ByteArrayToHexString(apduCommand));

            byte[] result = isoDep.transceive(apduCommand);


        }catch (Exception e){
            Log.e(TAG, "Error communicating with the card: " + e.toString());
        }
    }

    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
        //return HexStringToByteArray(SELECT_APDU_HEADER);
    }

    /**
     * Build APDU for GET_DATA command. See ISO 7816-4.
     *
     * @return APDU for SELECT AID command
     */
    public static byte[] BuildGetDataApdu() {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(GET_DATA_APDU_HEADER + "0FFF");
    }

    /**
     * Utility class to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Utility class to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     */
    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
