package com.example.my_plant;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MsgExchange {
    public static final int REQ_END = -3;
    public static final int REQ_BURN = -2;
    public static final int REQ_INIT = -1;

    private List<InputParamsStruct> paramsList = new ArrayList<>();

    // Listener for bt state
    private sendStateListener mSendStateListener = null;

    public interface sendStateListener {
        void setRequest(String msg);
        int getReqNum();
        void increaseReqNum();
    }

    private answerStateListener mAnswerStateListener = null;

    public interface answerStateListener {
        void endExchange();
    }

    public void setSendStateListener(sendStateListener listener) {
        mSendStateListener = listener;
    }

    public void setAnswerStateListener(answerStateListener listener) {
        mAnswerStateListener = listener;
    }

    public MsgExchange() {
    }

    public void sendGraph() {
        int reqNum;

        reqNum = mSendStateListener.getReqNum();

        if (mSendStateListener != null)
            mSendStateListener.setRequest(formMsg(reqNum));

    }

    public void recieveGraph(String incomeStr) {

        int reqNum = mSendStateListener.getReqNum();

        int maxReqNum = PersistentStorage.getIntProperty(PersistentStorage.MAX_REQ_NUM);
        Log.d("Check", "Max req: " + maxReqNum);

        if (reqNum >= maxReqNum) {

            if (mSendStateListener != null)
                mSendStateListener.setRequest(formMsg(REQ_END));

            if (maxReqNum != 0) {
                putInMemory();
            }

            if (mAnswerStateListener != null)
                mAnswerStateListener.endExchange();

            return;
        }

        switch (reqNum) {
            case REQ_INIT: {
                String paramsFromSlash = validateStr(incomeStr);

                if (paramsFromSlash == null) {
                    break;
                }

                String[] params = paramsFromSlash.split("/");
                if (params.length % 3 != 0) {
                    break;
                }

                //periodOfRecords = Long.parseLong(params[2]);
                //MaxReqNum = Integer.parseInt(params[0]);

                mSendStateListener.increaseReqNum();

                PersistentStorage.addLongProperty(PersistentStorage.PERIOD_OF_RECORDs, Long.parseLong(params[2]));
                PersistentStorage.addIntProperty(PersistentStorage.MAX_REQ_NUM, Integer.parseInt(params[0]));

                break;
            }
            default: {

                String paramsFromSlash = validateStr(incomeStr);
                if (paramsFromSlash == null) {
                    break;
                }

                String[] params = paramsFromSlash.split("/");
                if (params.length % 4 != 0) {
                    break;
                }

                paramsList.add(new InputParamsStruct(
                        Integer.parseInt(params[0]),          // humidity
                        Integer.parseInt(params[1]),          // temperature
                        Integer.parseInt(params[2]),          // light
                        Boolean.parseBoolean(params[3])));    // flgMoist

                mSendStateListener.increaseReqNum();

            }
        }

        sendGraph();

    }

    private void putInMemory() {
        /*for (int i = 1; i < MaxReqNum; i++) {
                    fillBD(activity, activity.salad, activity.paramsList.get(i), activity.periodOfRecords, i);
                }*/

        //String txtDateOfLastMoist = getLastBDDate(activity);

        Date now = new Date();

        PersistentStorage.addLongProperty(PersistentStorage.UPDATE_TIME_KEY, now.getTime());
        PersistentStorage.addLongProperty(PersistentStorage.WATER_TIME_KEY, (long) 0);//DateOfLastMoist ); //bd
        PersistentStorage.addIntProperty(PersistentStorage.HUMIDITY_KEY, paramsList.get(0).humidity);
        PersistentStorage.addIntProperty(PersistentStorage.TEMPERATURE_KEY, paramsList.get(0).temperature);
        PersistentStorage.addIntProperty(PersistentStorage.LIGHT_KEY, paramsList.get(0).light);
        paramsList.clear();
    }


/*
    void fillParamsBD(Context context, Salad salad, InputParamsStruct params,
                long reqPeriod, int reqNum) {
        //порядок данных: hum/temp/light/flgMoist

        SQLiteOpenHelper PlantBaseHelper = new PlantBaseHelper(context);
        SQLiteDatabase database = PlantBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        //TODO вычесть время прошедшее с последнего обновления
        calendar.add(Calendar.SECOND, (int) (reqPeriod * reqNum * (-1)));

        contentValues.put(DBPlant.PlantTable.Spec.NAME, salad.getName());
        contentValues.put(DBPlant.PlantTable.Spec.DATA, sdf_updating.format(calendar.getTime()));
        contentValues.put(DBPlant.PlantTable.Spec.HUMIDITY, params.humidity);
        contentValues.put(DBPlant.PlantTable.Spec.TEMPERATURE, params.temperature);
        contentValues.put(DBPlant.PlantTable.Spec.LIGHT, params.light);
        contentValues.put(DBPlant.PlantTable.Spec.MOISTURE, params.flgMoist);
        database.insert(DBPlant.PlantTable.dbNAME, null, contentValues);

    }*/


    private String validateStr(String incomeStr) {
        String str = null;

        StringBuilder sb = new StringBuilder();
        sb.append(incomeStr);

        int initialLineIndex = sb.indexOf("Data:");
        int finalLineIndex = sb.indexOf("\r\n");

        // Корректность начального и конечного символов
        if ((finalLineIndex > 0) && (initialLineIndex >= 0) && (finalLineIndex > initialLineIndex)) {
            str = sb.substring("Data:".length(), finalLineIndex);
        }

        // Корректность начального и конечного символов
        if (initialLineIndex >= 0) {
            str = sb.substring("Data:".length(), sb.length());
        }

        return str;
    }

    private String formMsg(int req) {
        String sendingMsg;

        switch (req) {
            case REQ_END:
                sendingMsg = "F" + "end" + "\t";
                break;
            case REQ_BURN:
                sendingMsg = "F" + "burn" + "\t";
                break;
            case REQ_INIT:
                sendingMsg = "F" + "init" + "\t";
                break;
            default:
                sendingMsg = "F" + req + "\t";
                break;
        }

        return sendingMsg;
    }


}
