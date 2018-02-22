package eu.x61.meter.meter;


import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import java.util.List;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugins.GeneratedPluginRegistrant;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends FlutterActivity implements SensorEventListener {
    private static final String LUXCHANNEL = "eu.x61/lux";
    private SensorManager mSensorManager;
    private Sensor mLightmeter;
    private float readLux = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        new MethodChannel(getFlutterView(), LUXCHANNEL).setMethodCallHandler(
                new MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, Result result) {
                        if (call.method.equals("getLuxLevel")) {
                            float luxLevel = getLux();

                            if (luxLevel != -1) {
                                result.success(luxLevel);
                            } else {
                                result.error("UNAVAILABLE", "Lightmeter not available", null);
                            }
                        } else {
                            result.notImplemented();
                        }
                    }
                }
        );
//SENSOR STUFF
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            //mLightmeter = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            List<Sensor> lm = mSensorManager.getSensorList(Sensor.TYPE_LIGHT);
            mLightmeter = lm.get(0);
        } else {
            throw new RuntimeException("Couldn't find a lightmeter");
        }

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLightmeter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        readLux = event.values[0];
    }

    public float getLux() {
        return readLux;
    }
}
