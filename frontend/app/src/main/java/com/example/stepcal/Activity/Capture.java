package com.example.stepcal.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.stepcal.API.ApiInterface;
import com.example.stepcal.DTO.CompletedTask;
import com.example.stepcal.R;
import com.example.stepcal.Retrofit.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.annotations.Marker;
import org.maplibre.android.annotations.MarkerOptions;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Capture extends BaseActivity
        implements SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final int ACTIVITY_RECOGNITION_REQUEST = 1002;

    private MapView mapView;
    private MapLibreMap mapLibreMap;

    private TextView statusText;
    private TextView stepsValue;
    private TextView distanceValue;
    private TextView caloriesValue;
    private TextView durationValue;
    private TextView startButton;

    private ImageButton mapLocationButton;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Marker currentLocationMarker;

    private boolean pendingLocationRequest = false;

    // =========================================================
    // STEP COUNTER
    // =========================================================

    private SensorManager sensorManager;

    private Sensor stepCounterSensor;
    private Sensor stepDetectorSensor;

    private boolean sensorRegistered = false;
    private boolean tracking = false;

    /*
     * Latest cumulative hardware step-counter value.
     *
     * Step Counter is the PRIMARY sensor.
     * Android reports the total number of steps since the sensor
     * was activated/rebooted/etc.
     */
    private int latestSensorSteps = -1;

    /*
     * Cumulative hardware value when START WALK is pressed.
     */
    private int startingSteps = -1;

    /*
     * Steps belonging only to the current walking session.
     */
    private int currentSteps = 0;

    // =========================================================
    // WALKING STATS
    // =========================================================

    private double distanceKm = 0.0;
    private double calories = 0.0;

    private long trackingStartTime = 0L;
    private long elapsedBeforeStop = 0L;

    private final Handler timerHandler = new Handler();

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            if (!tracking) {
                return;
            }

            updateDuration();

            timerHandler.postDelayed(this, 1000);
        }
    };

    // =========================================================
    // BACKEND CALORIE SAVE
    // =========================================================

    private ApiInterface apiInterface;
    private String authToken = "";

    /*
     * Prevents the same completed walk from being submitted
     * more than once.
     */
    private boolean walkCaloriesSaved = false;
    private boolean walkCaloriesSaving = false;

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MapLibre.getInstance(this);

        setContentView(R.layout.activity_capture);

        mapView = findViewById(R.id.mapView);

        statusText = findViewById(R.id.statusText);
        stepsValue = findViewById(R.id.stepsValue);
        distanceValue = findViewById(R.id.distanceValue);
        caloriesValue = findViewById(R.id.caloriesValue);
        durationValue = findViewById(R.id.durationValue);
        startButton = findViewById(R.id.startButton);

        mapLocationButton = findViewById(R.id.mapLocationButton);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        // =====================================================
        // BACKEND
        // =====================================================

        apiInterface =
                RetrofitClient.getRetrofit().create(ApiInterface.class);

        authToken = getSavedToken();

        // =====================================================
        // SENSOR SETUP
        // =====================================================

        sensorManager =
                (SensorManager) getSystemService(
                        Context.SENSOR_SERVICE
                );

        if (sensorManager != null) {

            /*
             * PRIMARY:
             * cumulative hardware step counter.
             */
            stepCounterSensor =
                    sensorManager.getDefaultSensor(
                            Sensor.TYPE_STEP_COUNTER
                    );

            /*
             * FALLBACK:
             * one event per detected step.
             */
            stepDetectorSensor =
                    sensorManager.getDefaultSensor(
                            Sensor.TYPE_STEP_DETECTOR
                    );
        }

        // =====================================================
        // MAP
        // =====================================================

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(map -> {

            mapLibreMap = map;

            map.setStyle(
                    "https://tiles.openfreemap.org/styles/liberty",
                    style -> {

                        map.getUiSettings()
                                .setZoomGesturesEnabled(true);

                        map.getUiSettings()
                                .setScrollGesturesEnabled(true);

                        map.getUiSettings()
                                .setRotateGesturesEnabled(true);

                        map.getUiSettings()
                                .setTiltGesturesEnabled(true);

                        TextView zoomInButton =
                                findViewById(R.id.zoomInButton);

                        TextView zoomOutButton =
                                findViewById(R.id.zoomOutButton);

                        if (zoomInButton != null) {

                            zoomInButton.setOnClickListener(v -> {

                                if (mapLibreMap != null) {

                                    mapLibreMap.animateCamera(
                                            CameraUpdateFactory.zoomIn()
                                    );
                                }
                            });
                        }

                        if (zoomOutButton != null) {

                            zoomOutButton.setOnClickListener(v -> {

                                if (mapLibreMap != null) {

                                    mapLibreMap.animateCamera(
                                            CameraUpdateFactory.zoomOut()
                                    );
                                }
                            });
                        }
                    }
            );
        });

        // =====================================================
        // LOCATION BUTTON
        // =====================================================

        if (mapLocationButton != null) {

            mapLocationButton.setOnClickListener(
                    v -> showCurrentLocation()
            );
        }

        // =====================================================
        // START / STOP
        // =====================================================

        startButton.setOnClickListener(v -> {

            if (!tracking) {

                startTracking();

            } else {

                stopTracking();
            }
        });

        // =====================================================
        // ACTIVITY RECOGNITION
        // =====================================================

        requestActivityRecognitionPermissionIfNeeded();
    }

    // =========================================================
    // STEP SENSOR PERMISSION
    // =========================================================

    private void requestActivityRecognitionPermissionIfNeeded() {

        if (android.os.Build.VERSION.SDK_INT >= 29) {

            if (
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACTIVITY_RECOGNITION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACTIVITY_RECOGNITION
                        },
                        ACTIVITY_RECOGNITION_REQUEST
                );

                return;
            }
        }

        startSensorListening();
    }

    // =========================================================
    // START SENSOR LISTENING
    // =========================================================

    private void startSensorListening() {

        if (sensorManager == null) {
            return;
        }

        if (sensorRegistered) {
            return;
        }

        /*
         * Android 10+ requires ACTIVITY_RECOGNITION.
         */
        if (
                android.os.Build.VERSION.SDK_INT >= 29
                        &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
        ) {

            return;
        }

        boolean registered = false;

        /*
         * PRIMARY SENSOR
         *
         * Step Counter gives the cumulative number of steps.
         * We calculate the session difference from it.
         */
        if (stepCounterSensor != null) {

            registered =
                    sensorManager.registerListener(
                            this,
                            stepCounterSensor,
                            SensorManager.SENSOR_DELAY_NORMAL
                    );
        }

        /*
         * FALLBACK SENSOR
         *
         * We also register Step Detector if available.
         *
         * IMPORTANT:
         * We do NOT use both simultaneously for counting.
         * Step Counter remains primary.
         */
        if (stepDetectorSensor != null) {

            boolean detectorRegistered =
                    sensorManager.registerListener(
                            this,
                            stepDetectorSensor,
                            SensorManager.SENSOR_DELAY_NORMAL
                    );

            registered =
                    registered || detectorRegistered;
        }

        sensorRegistered = registered;

        if (!registered) {

            statusText.setText(
                    "STEP SENSOR NOT AVAILABLE"
            );
        }
    }

    // =========================================================
    // STOP SENSOR LISTENING
    // =========================================================

    private void stopSensorListening() {

        if (
                sensorManager != null
                        && sensorRegistered
        ) {

            sensorManager.unregisterListener(this);

            sensorRegistered = false;
        }
    }

    // =========================================================
    // SENSOR EVENTS
    // =========================================================

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (
                event == null
                        || event.sensor == null
                        || event.values == null
                        || event.values.length == 0
        ) {

            return;
        }

        int sensorType =
                event.sensor.getType();

        // =====================================================
        // STEP COUNTER - PRIMARY
        // =====================================================

        if (
                sensorType
                        == Sensor.TYPE_STEP_COUNTER
        ) {

            int totalSteps =
                    Math.round(event.values[0]);

            latestSensorSteps =
                    totalSteps;

            /*
             * If a walk is active, calculate only the steps
             * taken since START WALK.
             */
            if (tracking) {

                if (startingSteps < 0) {

                    /*
                     * This normally only happens if the walk
                     * started before the first counter event.
                     */
                    startingSteps =
                            latestSensorSteps;

                    currentSteps = 0;

                } else {

                    currentSteps =
                            latestSensorSteps
                                    - startingSteps;

                    /*
                     * Protect against sensor reset/reboot.
                     */
                    if (currentSteps < 0) {

                        startingSteps =
                                latestSensorSteps;

                        currentSteps = 0;
                    }
                }

                calculateWalkingStats();

                updateDisplay();
            }

            return;
        }

        // =====================================================
        // STEP DETECTOR - FALLBACK ONLY
        // =====================================================

        if (
                sensorType
                        == Sensor.TYPE_STEP_DETECTOR
        ) {

            /*
             * NEVER use detector if a Step Counter exists.
             *
             * Otherwise both sensors could count the same
             * physical step and double the result.
             */
            if (stepCounterSensor != null) {
                return;
            }

            if (!tracking) {
                return;
            }

            currentSteps++;

            calculateWalkingStats();

            updateDisplay();
        }
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {
        // Not needed.
    }

    // =========================================================
    // START WALK
    // =========================================================

    private void startTracking() {

        /*
         * Android 10+ permission check.
         */
        if (
                android.os.Build.VERSION.SDK_INT >= 29
                        &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(
                    this,
                    "Allow physical activity permission first.",
                    Toast.LENGTH_LONG
            ).show();

            requestActivityRecognitionPermissionIfNeeded();

            return;
        }

        /*
         * Make sure sensors are listening.
         */
        startSensorListening();

        /*
         * A Step Counter must exist unless we have a detector
         * fallback.
         */
        if (
                stepCounterSensor == null
                        &&
                stepDetectorSensor == null
        ) {

            Toast.makeText(
                    this,
                    "This phone does not provide a supported step sensor.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        tracking = true;

        currentSteps = 0;

        distanceKm = 0.0;

        calories = 0.0;

        elapsedBeforeStop = 0L;

        trackingStartTime =
                System.currentTimeMillis();

        /*
         * New walk = new backend submission state.
         */
        walkCaloriesSaved = false;
        walkCaloriesSaving = false;

        // =====================================================
        // STEP COUNTER BASELINE
        // =====================================================

        if (stepCounterSensor != null) {

            /*
             * Best case:
             * we already received a hardware value before
             * START WALK was pressed.
             */
            if (latestSensorSteps >= 0) {

                startingSteps =
                        latestSensorSteps;

            } else {

                /*
                 * First sensor event after START WALK will
                 * establish the baseline.
                 */
                startingSteps = -1;
            }

        } else {

            /*
             * Step Detector fallback.
             *
             * Detector produces one event per step, so no
             * cumulative baseline is needed.
             */
            startingSteps = -1;
        }

        statusText.setText("WALKING");

        startButton.setText("STOP WALK");

        timerHandler.removeCallbacks(timerRunnable);

        timerHandler.post(timerRunnable);

        updateDisplay();

        Toast.makeText(
                this,
                "Walking started",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================================================
    // STOP WALK
    // =========================================================

    private void stopTracking() {

        if (trackingStartTime > 0) {

            elapsedBeforeStop =
                    System.currentTimeMillis()
                            - trackingStartTime;
        }

        /*
         * Capture final statistics before stopping.
         */
        calculateWalkingStats();

        tracking = false;

        timerHandler.removeCallbacks(timerRunnable);

        startButton.setText("START WALK");

        statusText.setText("WALK COMPLETE");

        updateDuration();

        updateDisplay();

        /*
         * Save completed walk calories exactly once.
         */
        saveWalkingCalories();
    }

    // =========================================================
    // SAVE WALKING CALORIES
    // =========================================================

    private void saveWalkingCalories() {

        if (walkCaloriesSaved || walkCaloriesSaving) {
            return;
        }

        if (calories <= 0.0) {

            Toast.makeText(
                    this,
                    "Walk completed. No calories to record yet.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (
                authToken == null
                        || authToken.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Walk completed, but you need to login again to save calories.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        walkCaloriesSaving = true;

        /*
         * calorie_burn = calories burned by this walk
         * calorie_intake = 0 because this is not food.
         */
        CompletedTask walkingTask =
                new CompletedTask(
                        calories,
                        0.0
                );

        apiInterface.addTask(
                "Bearer " + authToken,
                walkingTask
        ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Response<ResponseBody> response
            ) {

                walkCaloriesSaving = false;

                if (response.isSuccessful()) {

                    walkCaloriesSaved = true;

                    Toast.makeText(
                            Capture.this,
                            String.format(
                                    Locale.getDefault(),
                                    "Walk saved: %.2f kcal burned.",
                                    calories
                            ),
                            Toast.LENGTH_LONG
                    ).show();

                } else {

                    Toast.makeText(
                            Capture.this,
                            "Walk completed, but calories were not saved. Server error: "
                                    + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Throwable t
            ) {

                walkCaloriesSaving = false;

                Toast.makeText(
                        Capture.this,
                        "Walk completed, but calories could not be saved. Check your connection.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // =========================================================
    // WALKING STATS
    // =========================================================

    private void calculateWalkingStats() {

        /*
         * Average walking stride.
         */
        double distanceMeters =
                currentSteps * 0.75;

        distanceKm =
                distanceMeters / 1000.0;

        /*
         * Simple walking calorie estimate.
         */
        calories =
                currentSteps * 0.075;
    }

    // =========================================================
    // UPDATE DISPLAY
    // =========================================================

    private void updateDisplay() {

        if (stepsValue != null) {

            stepsValue.setText(
                    String.valueOf(currentSteps)
            );
        }

        if (distanceValue != null) {

            distanceValue.setText(
                    String.format(
                            Locale.US,
                            "%.2f km",
                            distanceKm
                    )
            );
        }

        if (caloriesValue != null) {

            caloriesValue.setText(
                    String.format(
                            Locale.US,
                            "%.0f kcal",
                            calories
                    )
            );
        }

        updateDuration();
    }

    // =========================================================
    // DURATION
    // =========================================================

    private void updateDuration() {

        long elapsed;

        if (tracking) {

            elapsed =
                    System.currentTimeMillis()
                            - trackingStartTime;

        } else {

            elapsed = elapsedBeforeStop;
        }

        long totalSeconds =
                elapsed / 1000;

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        if (durationValue != null) {

            durationValue.setText(
                    String.format(
                            Locale.US,
                            "%02d:%02d",
                            minutes,
                            seconds
                    )
            );
        }
    }

    // =========================================================
    // LOCATION
    // =========================================================

    private void showCurrentLocation() {

        boolean fineGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) {

            pendingLocationRequest = true;

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );

            return;
        }

        if (!isLocationEnabled()) {

            statusText.setText(
                    "LOCATION IS OFF"
            );

            Toast.makeText(
                    this,
                    "Turn on Location and try again.",
                    Toast.LENGTH_LONG
            ).show();

            try {

                startActivity(
                        new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                );

            } catch (Exception ignored) {
            }

            return;
        }

        statusText.setText(
                "FINDING YOUR LOCATION..."
        );

        int priority =
                fineGranted
                        ? Priority.PRIORITY_HIGH_ACCURACY
                        : Priority.PRIORITY_BALANCED_POWER_ACCURACY;

        CancellationTokenSource cancellationTokenSource =
                new CancellationTokenSource();

        fusedLocationClient
                .getCurrentLocation(
                        priority,
                        cancellationTokenSource.getToken()
                )
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        updateMapLocation(location);

                    } else {

                        /*
                         * Current location was unavailable.
                         * Try the last known location before
                         * starting a new location request.
                         */
                        getLastKnownLocation();
                    }

                })
                .addOnFailureListener(
                        e -> getLastKnownLocation()
                );
    }

    // =========================================================
    // LAST KNOWN LOCATION
    // =========================================================

    private void getLastKnownLocation() {

        boolean fineGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) {
            return;
        }

        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        updateMapLocation(location);

                    } else {

                        requestSingleLocationUpdate();
                    }
                })
                .addOnFailureListener(
                        e -> requestSingleLocationUpdate()
                );
    }

    // =========================================================
    // ONE-SHOT LOCATION FALLBACK
    // =========================================================

    private void requestSingleLocationUpdate() {

        boolean fineGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) {
            return;
        }

        LocationRequest request =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        1000
                )
                        .setMaxUpdates(1)
                        .setMinUpdateIntervalMillis(500)
                        .build();

        locationCallback =
                new LocationCallback() {

                    @Override
                    public void onLocationResult(
                            @NonNull LocationResult result
                    ) {

                        Location location =
                                result.getLastLocation();

                        if (location != null) {

                            updateMapLocation(location);

                        } else {

                            statusText.setText(
                                    "LOCATION NOT FOUND"
                            );

                            Toast.makeText(
                                    Capture.this,
                                    "Could not get your current location. Check GPS and try again.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                        if (locationCallback != null) {

                            fusedLocationClient
                                    .removeLocationUpdates(
                                            locationCallback
                                    );
                        }
                    }
                };

        fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                getMainLooper()
        );
    }

    // =========================================================
    // UPDATE MAP LOCATION
    // =========================================================

    private void updateMapLocation(
            Location location
    ) {

        if (mapLibreMap == null) {

            statusText.setText(
                    "MAP NOT READY"
            );

            return;
        }

        LatLng position =
                new LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                );

        mapLibreMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        position,
                        17.0
                )
        );

        if (currentLocationMarker != null) {

            currentLocationMarker.remove();
        }

        currentLocationMarker =
                mapLibreMap.addMarker(
                        new MarkerOptions()
                                .position(position)
                                .title("My Location")
                );

        statusText.setText(
                tracking
                        ? "WALKING"
                        : "LOCATION FOUND"
        );
    }

    // =========================================================
    // CHECK LOCATION ENABLED
    // =========================================================

    private boolean isLocationEnabled() {

        LocationManager locationManager =
                (LocationManager)
                        getSystemService(
                                Context.LOCATION_SERVICE
                        );

        if (locationManager == null) {
            return false;
        }

        try {

            return locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
            )
                    ||
                    locationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER
                    );

        } catch (Exception e) {

            return false;
        }
    }

    // =========================================================
    // PERMISSIONS
    // =========================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        // =====================================================
        // ACTIVITY RECOGNITION
        // =====================================================

        if (
                requestCode
                        == ACTIVITY_RECOGNITION_REQUEST
        ) {

            if (
                    android.os.Build.VERSION.SDK_INT < 29
                            ||
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {

                startSensorListening();

                statusText.setText(
                        "READY"
                );

            } else {

                statusText.setText(
                        "ACTIVITY PERMISSION NEEDED"
                );

                Toast.makeText(
                        this,
                        "Allow Physical Activity permission to count steps.",
                        Toast.LENGTH_LONG
                ).show();
            }

            return;
        }

        // =====================================================
        // LOCATION
        // =====================================================

        if (
                requestCode
                        == LOCATION_PERMISSION_REQUEST
                        &&
                pendingLocationRequest
        ) {

            pendingLocationRequest = false;

            boolean fineGranted =
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;

            boolean coarseGranted =
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;

            if (fineGranted || coarseGranted) {

                showCurrentLocation();

            } else {

                statusText.setText(
                        "LOCATION PERMISSION NEEDED"
                );

                Toast.makeText(
                        this,
                        "Allow location permission to use Find Me.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    // =========================================================
    // LIFECYCLE
    // =========================================================

    @Override
    protected void onStart() {

        super.onStart();

        if (mapView != null) {

            mapView.onStart();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (mapView != null) {

            mapView.onResume();
        }

        /*
         * Start sensors again whenever Capture becomes visible.
         */
        if (
                android.os.Build.VERSION.SDK_INT < 29
                        ||
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
        ) {

            startSensorListening();
        }
    }

    @Override
    protected void onPause() {

        stopSensorListening();

        if (mapView != null) {

            mapView.onPause();
        }

        super.onPause();
    }

    @Override
    protected void onStop() {

        if (mapView != null) {

            mapView.onStop();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        timerHandler.removeCallbacks(
                timerRunnable
        );

        stopSensorListening();

        if (locationCallback != null) {

            fusedLocationClient.removeLocationUpdates(
                    locationCallback
            );
        }

        if (mapView != null) {

            mapView.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();

        if (mapView != null) {

            mapView.onLowMemory();
        }
    }

    @Override
    protected void onSaveInstanceState(
            @NonNull Bundle outState
    ) {

        super.onSaveInstanceState(outState);

        if (mapView != null) {

            mapView.onSaveInstanceState(
                    outState
            );
        }
    }

    // =========================================================
    // AUTH TOKEN
    // =========================================================

    private String getSavedToken() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "MyPrefs",
                        MODE_PRIVATE
                );

        return preferences.getString(
                "token",
                ""
        );
    }
}