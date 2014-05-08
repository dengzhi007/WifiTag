package com.qihoo.wifitag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;



import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.Toast;

public class NFCUtil {
	//tnf=1 type=54 payload=02656E6C616F73686972656E
	
	public final static String[] tagInfo=new String[]{"request","ssid","password"};
	public static boolean isNFCDevice=true;
	private final static String textEncoding="UTF-8";
	
	private static Context context=null;
	
	private static NfcAdapter nfcAdapter=null;
	
	private static NdefMessage ndefMsg=null;
	
	private static PendingIntent pendingIntent=null;
	
	public static void init(Context c){
		System.out.println("intialing nfc...");
		context=c;
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(context);
		if (nfcAdapter == null)
			//Toast.makeText(context, "not nfc device", 50).show();
			isNFCDevice=false;
		else{
			if (!nfcAdapter.isEnabled()) 
				Toast.makeText(context, "not open nfc feature, quick pls...", 50).show();
		}
		
		pendingIntent=PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		if(pendingIntent == null)
			Toast.makeText(context, "pending intent new fail", 50).show();
		
		ndefMsg = new NdefMessage(new NdefRecord[] { getNdefRecord("message from wifitag") });
		if(ndefMsg == null)
			Toast.makeText(context, "get NdefMessage new fail", 50).show();
		
		
		

	}
	
	
	public static void enableForeground(Activity activity){
		
		if(!isAvailable())
			return;
		
		nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
		
		nfcAdapter.enableForegroundNdefPush(activity, NFCUtil.ndefMsg );
	}
	
	public static void disableForeground(Activity activity) {
		if (!isAvailable()) return;
		nfcAdapter.disableForegroundDispatch(activity);
		nfcAdapter.disableForegroundNdefPush(activity);
	}
	
	public static Boolean isAvailable(){
		if(nfcAdapter!=null && nfcAdapter.isEnabled())
			return true;
		else
			return false;
	}
	
	
	public static void startWriteTask(final String[] msg, final Intent intent,final Handler h){
		
		Thread thread=new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				
				
				
				if(writeMessage(msg, intent)){
					//success
					h.sendEmptyMessage(0x1234);
				}else{
					h.sendEmptyMessage(0x2234);
				}
				
			}
		};
		thread.start();

	}
	
	public static Boolean writeMessage(String[] msg, Intent intent){
		
		
		
		try {
			
		    Tag detectedTag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		    
		    NdefRecord[] records=new NdefRecord[msg.length];
		    
		    for(int i=0;i<msg.length;i++){
		    	records[i]=getNdefRecord(msg[i]);
		    }
		    
		    NdefMessage ndefMessage = new NdefMessage(records);
		    
		    writeMsgToTag(ndefMessage, detectedTag);
			
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	    return true;
	}
	
	private static boolean writeMsgToTag(NdefMessage message, Tag detectedTag){
		System.out.println("writing msg to tag");
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(detectedTag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    //Toast.makeText(context, "Tag is read-only.", Toast.LENGTH_LONG).show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    //Toast.makeText(context, "The data cannot written to tag, Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size + " bytes.", Toast.LENGTH_LONG).show();
                    return false;
                }

                ndef.writeNdefMessage(message);
                ndef.close();
                //Toast.makeText(context, "Message is written over to tag, message=" + message, Toast.LENGTH_LONG).show();
                return true;
            } else {
                NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
                if (ndefFormat != null) {
                    try {
                        ndefFormat.connect();
                        ndefFormat.format(message);
                        ndefFormat.close();
                        //Toast.makeText(context, "The data is written to the tag ", Toast.LENGTH_SHORT).show();
                        return true;
                    } catch (IOException e) {
                        //Toast.makeText(context, "Failed to format tag", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    //Toast.makeText(context, "NDEF is not supported", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (Exception e) {
            //Toast.makeText(context, "Write opreation is failed", Toast.LENGTH_SHORT).show();
        	return false;
        }

	}
	
    
    public static String[] readMessage(Intent intent){
    	
    	NdefMessage nMsg = readMsgFromTag(intent);

    	return parseNdefMsg(nMsg);
    }
	
	
	private static NdefMessage readMsgFromTag(Intent intent){
		

    	System.out.println("reading msg from tag");
    	Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] msgs;
        
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
            
        } else {
            // Unknown tag type
            byte[] empty = new byte[0];
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] payload = dumpTagData(tag).getBytes();
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
            NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
            msgs = new NdefMessage[] { msg };
            
        }       
        
		return msgs[0];
	}
	
    private static String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                case MifareClassic.TYPE_CLASSIC:
                    type = "Classic";
                    break;
                case MifareClassic.TYPE_PLUS:
                    type = "Plus";
                    break;
                case MifareClassic.TYPE_PRO:
                    type = "Pro";
                    break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                case MifareUltralight.TYPE_ULTRALIGHT:
                    type = "Ultralight";
                    break;
                case MifareUltralight.TYPE_ULTRALIGHT_C:
                    type = "Ultralight C";
                    break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }
	
    private static String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private static long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private static long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
    
    
    private static String[] parseNdefMsg(NdefMessage nMsg){
    	
    	NdefRecord[] records=nMsg.getRecords();
    	
    	String[] text=new String[tagInfo.length];
    	
    	int i=0;
    	
        for (final NdefRecord record : records) {
            //parse...

            if(record.getTnf()==NdefRecord.TNF_WELL_KNOWN &&Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)){
            	
            	try {
            		
            		if(i>=tagInfo.length)
            			break;
            		
                    byte[] payload = record.getPayload();
                   
                    String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0077;
                    String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                    
                    text[i] = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                    
                    i++;
                    
                } catch (UnsupportedEncodingException e) {
                    // should never happen unless we get a malformed tag.
                    throw new IllegalArgumentException(e);
                    
                }
            	
            }
            
        }
       
    	return text;
    }
    
    
    
    private static NdefRecord getNdefRecord(String msg){
        
    	Locale  locale = Locale.ENGLISH;
		
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

	    Charset utfEncoding = Charset.forName(textEncoding);
	    byte[] textBytes = msg.getBytes(utfEncoding);

	    int utfBit =  0;
	    char status = (char) (utfBit + langBytes.length);

	    byte[] data = new byte[1 + langBytes.length + textBytes.length];
	    data[0] = (byte) status;
	    System.arraycopy(langBytes, 0, data, 1, langBytes.length);
	    System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		
	    return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    	
    }
    
    
    
}
