package example.com.schoolbus.Util;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static void deleteEntity(Long serviceId,String entityName,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("ak","ZsdkuS4eVaLN2lfXePpwv1cWVP4UZleB")
                .add("service_id",String.valueOf(serviceId))
                .add("entity_name",entityName)
                .add("mcode","CB:71:30:9A:4B:6B:CC:DF:93:D9:CA:8B:D5:F4:11:66:26:17:7B:93;example.com.schoolbus")
                .build();
        Request request=new Request.Builder()
                .url("http://yingyan.baidu.com/api/v3/entity/add")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static void addEntity(Long serviceId,String entityName,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("ak","ZsdkuS4eVaLN2lfXePpwv1cWVP4UZleB")
                .add("service_id",String.valueOf(serviceId))
                .add("entity_name",entityName)
                .add("mcode","CB:71:30:9A:4B:6B:CC:DF:93:D9:CA:8B:D5:F4:11:66:26:17:7B:93;example.com.schoolbus")
                .build();
        Request request=new Request.Builder()
                .url("http://yingyan.baidu.com/api/v3/entity/delete")
                .post(requestBody)

                .build();
        client.newCall(request).enqueue(callback);
    }
   public static void uploadBusTrace(okhttp3.Callback callback){
        Long time=System.currentTimeMillis();
       OkHttpClient client=new OkHttpClient();
       RequestBody requestBody=new FormBody.Builder()
               .add("ak","ZsdkuS4eVaLN2lfXePpwv1cWVP4UZleB")
               .add("service_id","208725")
              .add("entity_name","Route1")
               .add("latitude","21.154694")
               .add("longitude","110.307156")
               .add("loc_time","1488785466")
               .add("coord_type_input","bd09ll")
              // .add("mcode","CB:71:30:9A:4B:6B:CC:DF:93:D9:CA:8B:D5:F4:11:66:26:17:7B:93;example.com.schoolbus")
               .build();
       Request request=new Request.Builder()
               .url("http://yingyan.baidu.com/api/v3/track/addpoint")
               .post(requestBody)
               .build();
       client.newCall(request).enqueue(callback);
    }
}
