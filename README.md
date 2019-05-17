## AirKissSDK 使用说明
### 概要
本文描述如何使用AirKissSDK，接入android应用使其对支持airkiss的智能硬件配置网络。  

### 配置gradle

     
    implementation 'cn.com.startai:airkisssdk:1.0.4'

### 接口调用  

>   设置接口回调  
 
 
         
    		//设置监听
        StartaiAirkissManager.getInstance().setAirKissListener(new C2JavaExDevice.OnAirKissListener() {
            @Override
            public void onAirKissSuccess() {
                appendLog("配置成功 用时 " + ((System.currentTimeMillis() - t) / 1000) + " s\n");
            }

            @Override
            public void onAirKissFailed(int error) {
                appendLog("配置失败 errorCode = " + error + "\n");
                StartaiAirkissManager.getInstance().stopAirKiss();
            }
        });



	 
     
 
>    开始配置  

		String pwd = "1234567890";//wifi密码
		String ssid = "MI_WIFI";//wifi 名称
		String aes = "";//不填
		long timeout = 1000 * 90;//超时时间 ms
		int processPeroid = 0;//配网发包流程间隔 0-5000 ms
        int datePeroid = 5; //配网每包数据之间间隔 5-80 ms
		//开始配置 
		StartaiAirkissManager.getInstance().startAirKiss(pwd, ssid, aesKey.getBytes(), timeout, processPeroid, datePeroid);


>    停止配置  

		//停止配置
		StartaiAirkissManager.getInstance().stopAirKiss();

### 权限  
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> 
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" /> 
### 注意事项 
- 配置网络接口回调是在子线程调用，请不要在此更新ui。
- 配置的wifi加密类型为OPEN时密码直接为“”空字符串。

###更新日志

- 2018-09-13 v1.0.4

		1，优化部分代码。

- 2018-09-13 v1.0.3

		1，调整接口调用方式。
 
- 2018-05-08 v1.0

		
		1，调整airkiss结果回调方式由handler调整为callback。
		2，添加airkiss-sender使用文档。 
	

- 2018-04-27 v1.0
 
		1，添加airkiss发送端demo项目
