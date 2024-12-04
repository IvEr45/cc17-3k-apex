package com.example.maindash.Gemini;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.maindash.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Information extends AppCompatActivity {
    private static final String API_KEY = "AIzaSyB4VkcyZzYvQDEcz5WWMF7NmYh6q46xgMY";  // Define your API key here
    private ProgressBar progressBar;
    private TextView generatedContentTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");
        ImageButton backButton = findViewById(R.id.backButton);

        // Set an onClickListener to handle the back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Construct the prompt dynamically
        progressBar = findViewById(R.id.progressBar);
        generatedContentTextView = findViewById(R.id.generatedContentTextView);

        progressBar.setVisibility(View.VISIBLE);
        generatedContentTextView.setText("Waiting for response...");


        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.15f;
        configBuilder.topK = 32;
        configBuilder.topP = 1f;
        configBuilder.maxOutputTokens = 4096;

        ArrayList<SafetySetting> safetySettings = new ArrayList<>();
        safetySettings.add(new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE));

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash-8b",
                API_KEY,  // Use the manually defined API key
                configBuilder.build(),
                safetySettings
        );

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("Tell me about this location specifically like what it's known for or it's surrounding areas as if you're a tour guide and don't ask for additional questions since you're just displaying information. For example a famous place is located near, by near I mean adjacent to it so don't site location that are not near to the place being described this location or a famous place is located here....And don't mention the name of the country or the postal code just the specific place:" + placeName)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // Switch to the main thread to update the UI
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                    generatedContentTextView.setText(result.getText()); // Update the TextView with the response
                });
            }

            @Override
            public void onFailure(Throwable t) {
                // Switch to the main thread to update the UI
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                    generatedContentTextView.setText("Error generating content"); // Show error message
                });
                t.printStackTrace(); // Log the error for debugging
            }
        }, executor);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button press
            onBackPressed();  // This will finish the current activity and navigate back to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
