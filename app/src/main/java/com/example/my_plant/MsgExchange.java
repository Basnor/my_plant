package com.example.my_plant;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.DBParams;

public class MsgExchange {
    Context context;

    public static final int REQ_END = -3;
    public static final int REQ_BURN = -2;
    public static final int REQ_INIT = -1;

    private List<InputParamsStruct> paramsList = new ArrayList<>();

    private DBParams mDBParams;

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

    public MsgExchange(Context context) {
        this.context = context;
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
                        Integer.parseInt(params[3])));        // flgMoist

                mSendStateListener.increaseReqNum();

            }
        }

        sendGraph();

    }

    private void putInMemory() {
        this.mDBParams = new DBParams(context);
        long id_profile = PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY);

        // Нет записей у нового профиля
        if (mDBParams.getParamsOfProfile(id_profile).isEmpty()) {
            firstTimeInit();
            return;
        }

        int maxReqNum = PersistentStorage.getIntProperty(PersistentStorage.MAX_REQ_NUM);
        long periodOfRecords = PersistentStorage.getLongProperty(PersistentStorage.PERIOD_OF_RECORDs);
        long lastRecTime = mDBParams.getLastDateToProfile(id_profile);

        SimpleDateFormat sdf_pattern = new SimpleDateFormat("dd-MM HH:mm:ss");
        Log.d("Check", "Last Date To Profile: " + sdf_pattern.format(lastRecTime));

        long recDate;

        for (int i = 1; i < maxReqNum; i++) {

            // Вычисляем дату сохранения i-ой записи в БД
            recDate = lastRecTime + periodOfRecords * 1000 * (maxReqNum - i);

            Log.d("Check", "NEXT Date in Profile: " + sdf_pattern.format(recDate));

            mDBParams.createParam(id_profile, recDate, paramsList.get(i).humidity,
                    paramsList.get(i).temperature, paramsList.get(i).light, paramsList.get(i).flgMoist);

        }

        Date now = new Date();

        // Находим дату последнего полива (примерно т.к. отображение в днях)
        long dateOfLastWater;
        if (paramsList.get(0).flgMoist > 0) {
            dateOfLastWater = now.getTime();
        } else {
            dateOfLastWater = mDBParams.getLastWaterDateToProfile(id_profile);
        }

        PersistentStorage.addLongProperty(PersistentStorage.UPDATE_TIME_KEY, now.getTime());
        PersistentStorage.addLongProperty(PersistentStorage.WATER_TIME_KEY, dateOfLastWater);
        PersistentStorage.addIntProperty(PersistentStorage.HUMIDITY_KEY, paramsList.get(0).humidity);
        PersistentStorage.addIntProperty(PersistentStorage.TEMPERATURE_KEY, paramsList.get(0).temperature);
        PersistentStorage.addIntProperty(PersistentStorage.LIGHT_KEY, paramsList.get(0).light);
        paramsList.clear();
        mDBParams.close();
    }

    private void firstTimeInit() {
        Log.d("Check", "Initialize logic");
        this.mDBParams = new DBParams(context);

        long id_profile = PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY);
        Date now = new Date();

        mDBParams.createParam(id_profile, now.getTime(), paramsList.get(0).humidity,
                paramsList.get(0).temperature, paramsList.get(0).light, paramsList.get(0).flgMoist);

        // Находим дату последнего полива (примерно т.к. отображение в днях)
        long dateOfLastWater;
        if (paramsList.get(0).flgMoist > 0) {
            dateOfLastWater = now.getTime();
        } else {
            dateOfLastWater = 0;
        }

        PersistentStorage.addLongProperty(PersistentStorage.UPDATE_TIME_KEY, now.getTime());
        PersistentStorage.addLongProperty(PersistentStorage.WATER_TIME_KEY, dateOfLastWater);
        PersistentStorage.addIntProperty(PersistentStorage.HUMIDITY_KEY, paramsList.get(0).humidity);
        PersistentStorage.addIntProperty(PersistentStorage.TEMPERATURE_KEY, paramsList.get(0).temperature);
        PersistentStorage.addIntProperty(PersistentStorage.LIGHT_KEY, paramsList.get(0).light);
        paramsList.clear();
        mDBParams.close();
    }

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
