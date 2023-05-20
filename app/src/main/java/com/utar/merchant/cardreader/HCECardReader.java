package com.utar.merchant.cardreader;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utar.merchant.MyApplication;
import com.utar.merchant.R;
import com.utar.merchant.data.Account;
import com.utar.merchant.data.Transaction;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public class HCECardReader implements NfcAdapter.ReaderCallback {
    IsoDep isoDep;

    Account payeeAccount, myAccount;
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]

    private static final String TAG = "E-wallet Card Reader";

    //AID for card services
    //private static final String WALLET_CARD_AID = "F222222222";
    private static final String WALLET_CARD_AID = "F222444888";

    // ISO-DEP command HEADER for selecting an AID.
    // Define the caller header

    // -> Select the corresponding Application ID
    private static final String SELECT_APDU_HEADER = "00A40400";
    // -> To get data
    private static final String GET_DATA_APDU_HEADER = "00CA0000";

    // -> To return status
    private static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};
    private static final byte[] UNKNOWN_CMD_SW = {(byte) 0x00, (byte)0x00};
    private static final byte[] TRANSACTION_SUCCESS = {(byte)0x91, (byte)0x92};
    private static final byte[] INSUFFICIENT_BALANCE = {(byte)0x91, (byte)0x93};

    // Weak reference to prevent retain loop. mAccountCallback is responsible for exiting
    // foreground mode before it becomes invalid (e.g. during onPause() or onStop()).
    private WeakReference<AccountCallback> mAccountCallback;

    public interface AccountCallback {
        public void setStatusText(int id);
        public void setAnimation(int rawRes, boolean repeat);
        public void countDownFinish();
        public double getAmount();

    }

    public HCECardReader(AccountCallback accountCallback) {
        mAccountCallback = new WeakReference<AccountCallback>(accountCallback);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered");

        isoDep = IsoDep.get(tag);

        if(tag == null){
            Log.e(TAG, "Tag is null object reference");
            return;
        }

        try{

            //connect to hce device
            isoDep.connect();

            mAccountCallback.get().setStatusText(R.string.please_wait);
            Log.i(TAG, "Timeout: " + isoDep.getTimeout());
            isoDep.setTimeout(3600);
            Log.i(TAG, "Timeout: " + isoDep.getTimeout());
            Log.i(TAG, "Max Transceive Length: " + isoDep.getMaxTransceiveLength());
            Log.i(TAG, "Requesting Remote AID: " + WALLET_CARD_AID);

            byte[] apduCommand = BuildSelectApdu(WALLET_CARD_AID);
            Log.i(TAG, "Connect to corresponding AID by using command: " + ByteArrayToHexString(apduCommand));

            byte[] result = isoDep.transceive(apduCommand);
            int resultLength = result.length;
            byte[] statusWord = {result[resultLength-2], result[resultLength-1]};
            byte[] payload = Arrays.copyOf(result, result.length - 2);


            if(Arrays.equals(SELECT_OK_SW, statusWord)){
                
                mAccountCallback.get().setStatusText(R.string.processing);
                mAccountCallback.get().setAnimation(R.raw.nfc_processing, true);

                String payeeID = new String(payload, "UTF-8");
                DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("user");
                DatabaseReference transactionDatabaseReference = FirebaseDatabase.getInstance().getReference("transactions");

                userDatabaseReference.child(payeeID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.i(TAG, "get payee data done");
                            payeeAccount = task.getResult().getValue(Account.class);
                            Log.i("Account Name: ", payeeAccount.getName());
                            double userBalance = Double.parseDouble(payeeAccount.getBalance());
                            double amount = mAccountCallback.get().getAmount();

                            if(userBalance <  amount){
                                Log.i(TAG, "Insufficient balance!");
                                mAccountCallback.get().setStatusText(R.string.insufficient_balance);
                                mAccountCallback.get().setAnimation(R.raw.card_fail, false);
                                mAccountCallback.get().countDownFinish();
                                try {
                                    isoDep.transceive(INSUFFICIENT_BALANCE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //close tag
                                try {
                                    isoDep.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                String finalBalance = String.format("%.2f", (userBalance-amount));
                                userDatabaseReference.child(payeeID).child("balance").setValue(finalBalance);

                                //transaction for payee
                                String userID = FirebaseAuth.getInstance().getUid();
                                userDatabaseReference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(Task<DataSnapshot> task) {
                                        if(task.isSuccessful()){
                                            Log.i(TAG, "User information retrieve successfully");
                                            myAccount = task.getResult().getValue(Account.class);

                                            //transaction for payee
                                            Transaction payeeTransaction = new Transaction(myAccount.getName(), amount, Transaction.PAYMENT);
                                            transactionDatabaseReference.child(payeeID).push().setValue(payeeTransaction);
                                            Log.i("My Account Name: ", myAccount.getName());

                                            //transaction for me
                                            Transaction myTransaction = new Transaction(payeeAccount.getName(), amount, Transaction.PAYMENT_RECEIVE);
                                            transactionDatabaseReference.child(userID).push().setValue(myTransaction);
                                            String myBalance = String.format("%.2f",Double.parseDouble(myAccount.getBalance()) + amount);
                                            userDatabaseReference.child(userID).child("balance").setValue(myBalance);

                                            //end activity
                                            try {
                                                byte[] amountByte = String.format("%.2f", amount).getBytes();
                                                byte[] result = isoDep.transceive(ConcatArrays(amountByte, TRANSACTION_SUCCESS));
                                                isoDep.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            mAccountCallback.get().setStatusText(R.string.payment_success);
                                            mAccountCallback.get().setAnimation(R.raw.nfc_finish, false);
                                            mAccountCallback.get().countDownFinish();
                                        }
                                        else{
                                            Log.e(TAG, "Firebase retrieve user information unsuccessful");
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            Log.e(TAG, "Firebase retrieve payee information unsuccessful");
                        }

                    }
                });
            }
            else{
                mAccountCallback.get().setAnimation(R.raw.card_fail, false);
                mAccountCallback.get().setStatusText(R.string.unknown_tag_detected);
                mAccountCallback.get().countDownFinish();
            }

            //isoDep.close();
        }catch (IOException e){
            Log.e(TAG, "IOException: " + e.getMessage());
            mAccountCallback.get().setStatusText(R.string.tag_disconnected);
            mAccountCallback.get().setAnimation(R.raw.card_fail, false);
            mAccountCallback.get().countDownFinish();
        }
        catch (Exception e){
            Log.e(TAG, "Error communicating with the card: " + e.toString());
            mAccountCallback.get().setStatusText(R.string.communication_error);
            mAccountCallback.get().setAnimation(R.raw.card_fail, false);
            mAccountCallback.get().countDownFinish();
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

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
