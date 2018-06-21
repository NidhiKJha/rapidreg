package org.unicef.rapidreg.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.unicef.rapidreg.exception.ObservableNullResponseException;
import org.unicef.rapidreg.model.RecordModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import io.reactivex.Observable;


public interface SyncTracingService {
    Observable<Response<ResponseBody>> getPhoto(String id, String photoKey, String
            photoSize);

    Observable<Response<ResponseBody>> getAudio(String id);

    Observable<Response<JsonElement>> get(String id, String locale, Boolean isMobile);

    Observable<Response<JsonElement>> getIds(String lastUpdate, Boolean isMobile);

    Response<JsonElement> uploadJsonProfile(RecordModel item) throws ObservableNullResponseException;

    void uploadAudio(RecordModel item) throws ObservableNullResponseException;

    Call<Response<JsonElement>> deletePhotos(String id, JsonArray photoKeys);

    void uploadPhotos(final RecordModel record);
}


