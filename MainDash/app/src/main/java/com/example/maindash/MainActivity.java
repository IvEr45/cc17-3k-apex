package com.example.maindash;

import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import com.example.maindash.AccountCreation.DatabaseHelper;
import com.example.maindash.AccountCreation.LoginActivity;
import com.example.maindash.AccountCreation.ProfileActivity;
import com.example.maindash.Gemini.Information;
import com.example.maindash.QRscan.Scan;
import com.example.maindash.Shop.ShopItem;
import com.example.maindash.Shop.ShopItemsAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import com.mapbox.geojson.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.search.autocomplete.PlaceAutocomplete;

import com.mapbox.search.ui.view.SearchResultsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    public String username;
    private TextView tvTotalScans;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    MapView mapView;
    FloatingActionButton focusLocationBtn;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineView routeLineView;
    private MapboxRouteLineApi routeLineApi;
    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {
        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
        }
    };
    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    Style style = mapView.getMapboxMap().getStyle$sdk_publicRelease();
                    if (style != null) {
                        routeLineView.renderRouteDrawData(style, routeLineErrorRouteSetValueExpected);
                    }
                }
            });
        }
    };
    boolean focusLocation = true;
    private MapboxNavigation mapboxNavigation;

    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(18.0).bearing(bearing).pitch(45.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocation = false;
            getGestures(mapView).removeOnMoveListener(this);
            focusLocationBtn.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Permission granted! Restart this app", Toast.LENGTH_SHORT).show();
            }
        }
    });
    Point burnham = Point.fromLngLat(120.59297039551518, 16.41258531755756);
    Point mines = Point.fromLngLat(120.62790115104846,116.4199125346858);
    Point session = Point.fromLngLat(120.59941625133591,16.410875935853085);
    Point john = Point.fromLngLat(120.61127403920067, 16.39710099127066);
    Point igorot = Point.fromLngLat(120.59466945318692,16.413260333373948);
    Point botanical = Point.fromLngLat(120.61289566667938, 16.415217614769723);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_logout_24);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        View headerView = navigationView.getHeaderView(0);
        TextView tvUserWelcome = headerView.findViewById(R.id.tv_user_welcome);
        tvTotalScans = headerView.findViewById(R.id.tv_total_scans);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");




        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        int totalScans = databaseHelper.getScanCount(username);


        tvTotalScans.setText("Points: " + totalScans);



        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                Toast.makeText(MainActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.remove("username");
                editor.apply();


                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();


                drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });


        FloatingActionButton fabOpenDrawer = findViewById(R.id.settings);
        fabOpenDrawer.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));


        DatabaseHelper finalDatabaseHelper = databaseHelper;
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) { }

            @Override
            public void onDrawerOpened(View drawerView) {
                int totalScans = finalDatabaseHelper.getScanCount(username);
                tvTotalScans.setText("Points: " + totalScans);
            }

            @Override
            public void onDrawerClosed(View drawerView) { }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });




        username = getIntent().getStringExtra("username");
        String username = getIntent().getStringExtra("username");
        tvUserWelcome.setText("Welcome, " + username + "!");

        FloatingActionButton fabShop= findViewById(R.id.shop);
        fabShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });


        FloatingActionButton fabProfile = findViewById(R.id.profile);
        fabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        FloatingActionButton fabCustomizeMaps = findViewById(R.id.style);
        fabCustomizeMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMapStyleDialog();
            }
        });
        FloatingActionButton fabQrScan = findViewById(R.id.scan);
        fabQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Scan.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });


        mapView = findViewById(R.id.mapView);
        focusLocationBtn = findViewById(R.id.focusLocation);

        MapboxRouteLineOptions options = new MapboxRouteLineOptions.Builder(this).withRouteLineResources(new RouteLineResources.Builder().build())
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER).build();
        routeLineView = new MapboxRouteLineView(options);
        routeLineApi = new MapboxRouteLineApi(options);

        NavigationOptions navigationOptions = new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build();

        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);

        mapboxNavigation.registerRoutesObserver(routesObserver);
        mapboxNavigation.registerLocationObserver(locationObserver);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {

            mapboxNavigation.startTripSession();
        }




        MaterialButton stopRoute = findViewById(R.id.stopRoute);
        focusLocationBtn.hide();
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });

                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.qr);
                Bitmap bitmap = drawableToBitmap(drawable);
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

                PointAnnotationOptions pointAnnotationOptionsFeatured = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                        .withPoint(mines);

                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                        .withPoint(burnham);
                PointAnnotationOptions pointAnnotationOptions2 = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                        .withPoint(session);
                PointAnnotationOptions pointAnnotationOptions3 = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                        .withPoint(john);
                PointAnnotationOptions pointAnnotationOptions4 = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                        .withPoint(igorot);
                PointAnnotationOptions pointAnnotationOptions5 = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                        .withPoint(botanical);

                pointAnnotationManager.create(pointAnnotationOptionsFeatured);
                pointAnnotationManager.create(pointAnnotationOptions);
                pointAnnotationManager.create(pointAnnotationOptions2);
                pointAnnotationManager.create(pointAnnotationOptions3);
                pointAnnotationManager.create(pointAnnotationOptions4);
                pointAnnotationManager.create(pointAnnotationOptions5);


                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {

                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        fetchRoute(point);
                        return true;
                    }
                });


                stopRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mapboxNavigation.setNavigationRoutes(Collections.emptyList());
                    }
                });

                focusLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocation = true;
                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        focusLocationBtn.hide();
                    }
                });

            }

        });

    }
    public void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView tvScanCount = bottomSheetView.findViewById(R.id.tv_scan_count1);
        int scanCount = databaseHelper.getScanCount(username);
        tvScanCount.setText("Points: " + scanCount);

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.rv_shop_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        List<ShopItem> shopItems = new ArrayList<>();
        shopItems.add(new ShopItem("Points Required: 10", "Ube Jam", R.drawable.ube));
        shopItems.add(new ShopItem("Points Required: 35", "Strawberry Jam", R.drawable.straw));
        shopItems.add(new ShopItem("Points Required: 100", "Lengua de Gato", R.drawable.lengua));
        shopItems.add(new ShopItem("Points Required: 10", "Sundot Kulangot", R.drawable.sundot));
        shopItems.add(new ShopItem("Points Required: 20", "Handicrafts and Woven Products", R.drawable.hand));
        shopItems.add(new ShopItem("Points Required: 25", "Walis", R.drawable.walis));
        shopItems.add(new ShopItem("Points Required: 50", "Fruit Wines", R.drawable.wine));
        shopItems.add(new ShopItem("Points Required: 200", "Handcrafted Accessories", R.drawable.craft));
        shopItems.add(new ShopItem("Points Required: 75", "Fresh Vegetables", R.drawable.veg));
        shopItems.add(new ShopItem("Points Required: 40", "Ube Jam", R.drawable.honey));


        ShopItemsAdapter adapter = new ShopItemsAdapter(this, shopItems);
        recyclerView.setAdapter(adapter);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }





    @SuppressLint("MissingPermission")
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

        private void showMapStyleDialog () {
            String[] mapStyles = {"Streets", "Light", "Dark", "Satellite Streets"};
            String[] mapStyleUris = {
                    Style.MAPBOX_STREETS,
                    Style.LIGHT,
                    Style.DARK,
                    Style.SATELLITE_STREETS,
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Map Style");
            builder.setItems(mapStyles, (dialog, which) -> {
                String selectedStyleUri = mapStyleUris[which];
                mapView.getMapboxMap().loadStyleUri(selectedStyleUri, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                    }
                });
            });
            builder.show();
        }




        @SuppressLint("MissingPermission")
        private void fetchRoute(Point point) {
            LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(MainActivity.this);
            locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    Location location = result.getLastLocation();
                    RouteOptions.Builder builder = RouteOptions.builder();
                    Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                    builder.coordinatesList(Arrays.asList(origin, point));
                    builder.alternatives(false);
                    builder.profile(DirectionsCriteria.PROFILE_DRIVING);
                    builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                    applyDefaultNavigationOptions(builder);

                    mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                        @Override
                        public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                            mapboxNavigation.setNavigationRoutes(list);
                            focusLocationBtn.performClick();
                        }

                        @Override
                        public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                            Toast.makeText(MainActivity.this, "Route request failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                        }
                    });
                }
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);


        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);

        Log.d("MainActivity", "Received Coordinates: Latitude=" + latitude + ", Longitude=" + longitude);

        if (latitude != 0.0 && longitude != 0.0) {
            updateMapCoordinates(latitude, longitude);
        } else {
            Log.e("MainActivity", "Invalid coordinates received.");
        }
    }


    private void updateMapCoordinates(double latitude, double longitude) {
        Point newPoint = Point.fromLngLat(longitude, latitude);
        fetchPlaceName(newPoint);
    }
    private void fetchPlaceName(Point point) {

        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(point.longitude(), point.latitude()))
                .build();

        // Execute the geocoding request asynchronously
        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                if (response.body() != null && !response.body().features().isEmpty()) {
                    CarmenFeature feature = response.body().features().get(0);
                    String placeName = feature.placeName();
                    showToast(placeName);
                    Intent intent = new Intent(MainActivity.this, Information.class);
                    intent.putExtra("placeName", placeName);
                    startActivity(intent);

                } else {
                    showToast("No place found at this location");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) {
                showToast("Error fetching place name");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }





    @Override
        protected void onDestroy () {
            super.onDestroy();
            mapboxNavigation.onDestroy();
            mapboxNavigation.unregisterRoutesObserver(routesObserver);
            mapboxNavigation.unregisterLocationObserver(locationObserver);
        }

    }
