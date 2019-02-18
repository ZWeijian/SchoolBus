package example.com.schoolbus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.Trace;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.ClearCacheTrackRequest;
import com.baidu.trace.api.track.ClearCacheTrackResponse;
import com.baidu.trace.api.track.DistanceRequest;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import example.com.schoolbus.Util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class TraceActivity extends AppCompatActivity {

boolean isFirstLocate=true;

    // 请求标识
    int tag = 1;
    // 轨迹服务ID
   long serviceId = 208724;

    // 设备标识
   String entityName = "myTrace";


    ArrayList<String> permissionList=new ArrayList<>();
    LocationClient locationClient;

private Button startGather;

    private Button stopGather;
private Chronometer timer;

   private MapView mapView;
   private BaiduMap baiduMap;

   private TextView text;
   Trace mTrace;
    LBSTraceClient mTraceClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_trace);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    //保持屏幕常亮

    // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        addToWhiteList();


        mapView=(MapView)findViewById(R.id.map);
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        startGather=(Button)findViewById(R.id.start_gather);
        stopGather=(Button)findViewById(R.id.stop_gather);
        timer=(Chronometer)findViewById(R.id.timer);

        text=(TextView)findViewById(R.id.text);
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

      if(ContextCompat.checkSelfPermission(TraceActivity.this,Manifest.permission.READ_PHONE_STATE)!=PERMISSION_GRANTED){

            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(TraceActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(TraceActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(TraceActivity.this,permissions,1);

        }else {
            requestLocation();
        }

        drawRoute();
// 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
// 初始化轨迹服务
       final Trace mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);
// 初始化轨迹服务客户端
       mTraceClient = new LBSTraceClient(getApplicationContext());
        // 定位周期(单位:秒)
        int gatherInterval = 3;
// 打包回传周期(单位:秒)
        int packInterval = 6;
// 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);
//开启服务,onCreate()末尾
        mTraceClient.startTrace(mTrace, mTraceListener);
        Log.d("zhu","startTrace");




startGather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开启采集

                mTraceClient.startGather(mTraceListener);
                Log.d("zhu","startGather");
                //查询轨迹并展示
                queryTrace();

                    //计时器清零并开始
                    timer.setBase(SystemClock.elapsedRealtime());//计时器清零

                int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
               timer.setFormat("0"+String.valueOf(hour)+":%s");
            timer.start();
            }
        });
stopGather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止采集
                mTraceClient.stopGather(mTraceListener);
                Log.d("zhu","stopGather");

                //清除手机缓存轨迹
                List<String> list=new ArrayList<>();
                list.add(entityName);
                ClearCacheTrackRequest request=new ClearCacheTrackRequest(tag,serviceId);
                request.setEntityNames(list);
                mTraceClient.clearCacheTrack(request, new OnTrackListener(){
                    @Override
                    public void onClearCacheTrackCallback(ClearCacheTrackResponse clearCacheTrackResponse) {
                        super.onClearCacheTrackCallback(clearCacheTrackResponse);
                    }
                });
                //查询里程
                queryDistance();

                //清除控制台历史轨迹
                clearTrace();
               //时间停止
                timer.stop();

                int h = Integer.parseInt(timer.getText().toString().split(":")[0]);
                int m =Integer.parseInt(timer.getText().toString().split(":")[1]);
                int s = Integer.parseInt(timer.getText().toString().split(":")[2]);
                Log.d("zhu","一共"+h+"小时"+m+"分钟"+s+"秒");

                //停止服务
                mTraceClient.stopTrace(mTrace, mTraceListener);
                Log.d("zhu","stopTrace");
            }
        });

    }



    //查询轨迹
private  void queryTrace(){

// 创建历史轨迹请求实例
    HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(tag, serviceId, entityName);
// 设置需要纠偏
    historyTrackRequest.setProcessed(true);
// 创建纠偏选项实例
    ProcessOption processOption = new ProcessOption();
// 设置需要去噪
    processOption.setNeedDenoise(true);
// 设置需要抽稀
    processOption.setNeedVacuate(true);
// 设置需要绑路
   processOption.setNeedMapMatch(true);
// 设置精度过滤值(定位精度大于100米的过滤掉)
   // processOption.setRadiusThreshold(100);
// 设置交通方式为驾车
      processOption.setTransportMode(TransportMode.driving);
// 设置纠偏选项
    historyTrackRequest.setProcessOption(processOption);
//设置轨迹查询起止时间
// 开始时间(单位：秒)
    long startTime =(int)(System.currentTimeMillis() / 1000 - 24* 60 * 60);
// 结束时间(单位：秒)
    long endTime =(int)( System.currentTimeMillis() / 1000);      //取最近24小时
// 设置开始时间
    historyTrackRequest.setStartTime(startTime);
// 设置结束时间
    historyTrackRequest.setEndTime(endTime);
    /**
     * 轨迹排序规则
     */
     SortType sortType = SortType.asc;
//分页大小
    int pageSize = 5000;
//分页索引
    int pageIndex = 1;
historyTrackRequest.setPageIndex(pageIndex);
historyTrackRequest.setPageSize(pageSize);
// 初始化轨迹监听器
    OnTrackListener mTrackListener = new OnTrackListener() {

        // 历史轨迹回调
        @Override
        public void onHistoryTrackCallback(HistoryTrackResponse response) {
            try {


                int toal = response.getTotal();
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    Toast.makeText(TraceActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (0 == toal) {
                    Toast.makeText(TraceActivity.this, "未查询到轨迹！", Toast.LENGTH_SHORT).show();
                }


                Log.d("zhu", "response.getEntityName()==" + response.getEntityName());
                Log.d("zhu", "response.getDistance()==" + response.getDistance());
                Log.d("zhu", "response.getMessage()==" + response.getMessage());
                List<TrackPoint> trackPoints = response.getTrackPoints();
                List<LatLng> points = new ArrayList<>();

                for (int i = 0; i < trackPoints.size(); i++) {
                    com.baidu.trace.model.LatLng ll = trackPoints.get(i).getLocation();
                    LatLng latLng = new LatLng(ll.latitude, ll.longitude);
                    points.add(latLng);
                }

                Log.d("wei", "points总数=" + points.size());
                for (int i = 0; i < trackPoints.size(); i++) {
                    Log.d("wei", "纬度=" + points.get(i).latitude);
                    Log.d("wei", "经度=" + points.get(i).longitude);
                }
                if (points.size() > 2) {
                    //每次画路线之前先清空
                    baiduMap.clear();
                    OverlayOptions mOverlayOptions = new PolylineOptions()
                            .width(10)
                            .color(0xAAFF0000)
                            .points(points);
                    //在地图上绘制折线
                    //mPloyline 折线对象
                    Overlay mPolyline = baiduMap.addOverlay(mOverlayOptions);
                    requestLocation();
                }
            }catch (NullPointerException e){
                Toast.makeText(TraceActivity.this,"网络不可用，请尽快连接",Toast.LENGTH_SHORT).show();
                Log.d("guud",e.getMessage());
                e.printStackTrace();
            }
        }
    };
// 查询轨迹
    mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener);
}


    @Override
    protected void onResume() {

        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        super.onDestroy();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        //停止服务
        mTraceClient.stopTrace(mTrace, mTraceListener);
        Log.d("zhu","stopTrace");
  }


    private void requestLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setCoorType("bd09ll");
        locationClient.setLocOption(option);
        locationClient.start();
    }

    /**
     * Android6.0申请权限的回调方法
     */


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {    //申请权限后，返回结果的处理
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }


    //获取本机位置的监听器
    public  class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

                LatLng ll=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(update);
                if(isFirstLocate){
                    MapStatusUpdate update1=MapStatusUpdateFactory.zoomTo(16f);
                    baiduMap.animateMapStatus(update1);
                    isFirstLocate=false;
                }else{
                    //查询轨迹并绘制
                    queryTrace();
                }
            //在地图上显示本稽位置
            MyLocationData.Builder builder=new MyLocationData.Builder();
            builder.longitude(bdLocation.getLongitude());
            builder.latitude(bdLocation.getLatitude());
            MyLocationData data=builder.build();
            baiduMap.setMyLocationData(data);
           String loginText= getIntent().getStringExtra("information");
            String str="纬度："+bdLocation.getLatitude()
                    +"\n经度："+bdLocation.getLongitude()
                    +"\n国家："+bdLocation.getCountry()
                    +"\n省："+bdLocation.getProvince()
                    +"\n市："+bdLocation.getCity()
                    +"\n区："+bdLocation.getDistrict()
                    +"\n街道："+bdLocation.getStreet()
                    +"\n位置描述："+bdLocation.getLocationDescribe()
                    +"\n错误码："+  bdLocation.getLocType();
            if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                str=str+"\n定位方式：网络\n";
            }else if (bdLocation.getLocType()==BDLocation.TypeGpsLocation) {
                str = str + "\n定位方式：GPS\n";
            }
            text.setText(str+loginText);
        }
    }


    public OnTraceListener mTraceListener = new OnTraceListener() {
        @Override public void onBindServiceCallback(int i, String s) {}
        @Override public void onStartTraceCallback(int status, String message) {}  // 开启服务回调
        @Override public void onStopTraceCallback(int status, String message) {}// 停止服务回调
        @Override public void onStartGatherCallback(int status, String message) {}       // 开启采集回调
        @Override public void onStopGatherCallback(int status, String message) {}// 停止采集回调
        @Override public void onInitBOSCallback(int i, String s) { }
        @Override public void onPushCallback(byte messageNo, PushMessage message) {}   // 推送回调
    };


    //清除历史轨迹,先删除控制台的entity再创建一个新的entity
    private void clearTrace(){
        //删除entity
        HttpUtil.deleteEntity(serviceId,entityName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("jian","删除请求失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("jian","删除");
                Log.d("jian","response=="+response.body().string());

            }
        });

        //创建entity
        HttpUtil.addEntity(serviceId,entityName,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("jian","添加失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("jian","添加成功");
                Log.d("jian","response=="+response.body().string());
            }
        });
    }


    //请求3小时内的轨迹里程，并进行轨迹纠偏和驾车里程补偿
    public void queryDistance(){
        int tag = 2;  // 请求标识
        DistanceRequest distanceRequest = new DistanceRequest(tag, serviceId, entityName);// 创建里程查询请求实例
        long startTime = System.currentTimeMillis() / 1000 - 3 * 60 * 60;// 开始时间(单位：秒)
        long endTime = System.currentTimeMillis() / 1000;// 结束时间(单位：秒)
        distanceRequest.setStartTime(startTime);// 设置开始时间
        distanceRequest.setEndTime(endTime);// 设置结束时间
        distanceRequest.setProcessed(true);// 设置需要纠偏
        ProcessOption processOption = new ProcessOption();// 创建纠偏选项实例
        processOption.setNeedDenoise(true);// 设置需要去噪
        processOption.setNeedMapMatch(true);// 设置需要绑路
        processOption.setTransportMode(TransportMode.driving);// 设置交通方式为驾车
        distanceRequest.setProcessOption(processOption);// 设置纠偏选项
        distanceRequest.setSupplementMode(SupplementMode.driving);// 设置里程填充方式为驾车
        // 初始化轨迹监听器
        OnTrackListener mTrackListener = new OnTrackListener() {
            // 里程回调
            @Override
            public void onDistanceCallback(DistanceResponse response) { Toast.makeText(TraceActivity.this,"里程："+response.getDistance()+"米",Toast.LENGTH_SHORT).show(); }};
          // 查询里程
       mTraceClient.queryDistance(distanceRequest, mTrackListener);
    }



    //绘制公交车标记mark
    public void drawRoute() {


        Map<String, LatLng> map = new HashMap<>();
        map.put("霞山校区", new LatLng(21.204721, 110.417187));
        map.put("—文体路", new LatLng(21.204496, 110.416084));
        map.put("文明东路（市政大厦站）", new LatLng(21.208096, 110.413625));
        map.put("文明中路站", new LatLng(21.203849110, 110.409718));
        map.put("文明西路站", new LatLng(110.40534, 419956));
        map.put("建新西路", new LatLng(21.198086, 110.406263));
        map.put("工农路", new LatLng(21.194579, 110.409979));
        map.put("湖光校区", new LatLng(21.154694, 110.307156));
        for (Map.Entry<String, LatLng> entry : map.entrySet()) {
           //定义Maker坐标点
            LatLng point = new LatLng(entry.getValue().latitude, entry.getValue().longitude);
           //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.b);
//构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point) //必传参数
                    .icon(bitmap) //必传参数
                    .draggable(false)
                    //开启近大远小效果
                    .perspective(true)
                    .title(entry.getKey())
//设置平贴地图，在地图中双指下拉查看效果
                    .flat(true)
                    .alpha(0.7f);
//在地图上添加Marker，并显示
            Overlay overlay=baiduMap.addOverlay(option);
            TextOptions textOptions = new TextOptions()
                    .bgColor(0xAAFFFF00)  //設置文字覆盖物背景颜色
                    .fontSize(40)  //设置字体大小
                    .fontColor(0xFFFF00FF)// 设置字体颜色
                    .text(entry.getKey())  //文字内容
                    .rotate(0)  //设置文字的旋转角度
                    .position(point);

            overlay = baiduMap.addOverlay(textOptions );//地图上添加mark

        }
        //为mark设置点击事件
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(TraceActivity.this,marker.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


    private void addToWhiteList(){
        // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = "example.com.schoolbus";
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoring) {
                Intent intent = new Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                try {
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
