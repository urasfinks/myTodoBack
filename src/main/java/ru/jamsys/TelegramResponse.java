package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.util.PersonUtil;
import ru.jamsys.util.Util;

import java.math.BigDecimal;
import java.util.Map;

public class TelegramResponse {

    public String resp = null;

    public void setResponse(String resp){
        this.resp = resp;
    }

    private boolean isSuccess() {
        if (resp != null) {
            Map parsedResp = new Gson().fromJson(resp, Map.class);
            Double errorCode = (Double) Util.selector(parsedResp, "error_code", null);
            if (errorCode != null && errorCode == 403) {
                return false;
            }
        }
        return true;
    }

    public void checkSuccess(BigDecimal idPerson){
        if(!isSuccess()){
            PersonUtil.removeIdChatTelegram(idPerson);
        }
    }

}
