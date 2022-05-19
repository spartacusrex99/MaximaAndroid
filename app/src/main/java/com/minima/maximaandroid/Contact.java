package com.minima.maximaandroid;

import org.minima.utils.json.JSONObject;

import java.io.Serializable;

public class Contact implements Serializable {

    public static final int STATUS_IDLE     = 0;
    public static final int STATUS_RED      = 1;
    public static final int STATUS_YELLOW   = 2;
    public static final int STATUS_GREEN    = 3;

    public long mID          = 0;

    public String mName      = "";
    public String mPublicKey = "";
    public String mAddress   = "";
    public String mMyAddress   = "";

    public String mMinimaAddress   = "";

    public  long  mLastSeen = System.currentTimeMillis();

    public boolean mSameChain = false;

    public  int  mStatus = STATUS_GREEN;

    public Contact(){}

    public Contact(String zName){
        mName = zName;
    }

    public Contact(JSONObject zJSONContact){
        mID         = (long)zJSONContact.get("id");
        mPublicKey  = (String)zJSONContact.get("publickey");
        mAddress    = (String)zJSONContact.get("currentaddress");
        mMyAddress  = (String)zJSONContact.get("myaddress");
        mLastSeen   = (long)zJSONContact.get("lastseen");
        mSameChain  = (boolean)zJSONContact.get("samechain");

        //Get the Extra Data
        JSONObject extradata    = (JSONObject)zJSONContact.get("extradata");
        mName                   = (String)extradata.get("name");
        mMinimaAddress          = (String)extradata.get("minimaaddress");
    }

    public boolean getStatus(){

        long timenow = System.currentTimeMillis();
        if(timenow - mLastSeen < 1000 * 60 * 30 && mSameChain){
            return true;
        }

        return false;
    }

    public boolean getConnectionInTime(){
        long timenow = System.currentTimeMillis();
        if(timenow - mLastSeen < 1000 * 60 * 30){
            return true;
        }
        return false;
    }

}
