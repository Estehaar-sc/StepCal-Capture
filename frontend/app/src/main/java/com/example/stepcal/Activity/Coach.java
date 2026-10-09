package com.example.stepcal.Activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.stepcal.API.ApiInterface;
import com.example.stepcal.DTO.CompletedTask;
import com.example.stepcal.DTO.Task;
import com.example.stepcal.DTO.User;
import com.example.stepcal.R;
import com.example.stepcal.Retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Coach extends BaseActivity {

    private EditText messageInput;
    private LinearLayout chatContainer;
    private ScrollView chatScroll;
    private LinearLayout suggestionsSection;

    private ApiInterface apiInterface;
    private String authToken;

    private User userProfile;
    private Task dailyTask;
    private CompletedTask todayCompletedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coach);

        messageInput = findViewById(R.id.messageInput);
        chatContainer = findViewById(R.id.chatContainer);
        chatScroll = findViewById(R.id.chatScroll);
        suggestionsSection = findViewById(R.id.suggestionsSection);

        apiInterface =
                RetrofitClient.getRetrofit()
                        .create(ApiInterface.class);

        SharedPreferences preferences =
                getSharedPreferences(
                        "MyPrefs",
                        MODE_PRIVATE
                );

        authToken =
                preferences.getString(
                        "token",
                        ""
                );

        findViewById(R.id.sendButton)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendMessage();
                            }
                        }
                );

        findViewById(R.id.suggestionToday)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                messageInput.setText(
                                        "How am I doing today?"
                                );

                                hideSuggestions();
                                sendMessage();
                            }
                        }
                );

        findViewById(R.id.suggestionFood)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                messageInput.setText(
                                        "What should I eat today?"
                                );

                                hideSuggestions();
                                sendMessage();
                            }
                        }
                );

        findViewById(R.id.suggestionWorkout)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                messageInput.setText(
                                        "What workout should I do?"
                                );

                                hideSuggestions();
                                sendMessage();
                            }
                        }
                );
    }

    private void sendMessage() {

        String message =
                messageInput.getText()
                        .toString()
                        .trim();

        if (message.isEmpty()) {
            return;
        }

        hideSuggestions();

        addUserMessage(message);

        messageInput.setText("");

        addCoachMessage(
                "Checking your StepCal data..."
        );

        loadStepCalContext(message);
    }

    private void loadStepCalContext(
            String userMessage) {

        if (authToken == null
                || authToken.trim().isEmpty()) {

            replaceLastCoachMessage(
                    "Please log in again so I can access your StepCal data."
            );

            return;
        }

        apiInterface
                .getProfile(
                        "Bearer " + authToken
                )
                .enqueue(
                        new Callback<User>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<User> call,
                                    @NonNull Response<User> response
                            ) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    replaceLastCoachMessage(
                                            "I couldn't load your StepCal profile. Please try again."
                                    );

                                    return;
                                }

                                userProfile =
                                        response.body();

                                loadDailyTask(
                                        userMessage
                                );
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<User> call,
                                    @NonNull Throwable t
                            ) {

                                replaceLastCoachMessage(
                                        "I couldn't connect to your StepCal profile. Please try again."
                                );
                            }
                        }
                );
    }

    private void loadDailyTask(
            String userMessage) {

        apiInterface
                .getTask(
                        "Bearer " + authToken
                )
                .enqueue(
                        new Callback<Task>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<Task> call,
                                    @NonNull Response<Task> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    dailyTask =
                                            response.body();

                                } else {

                                    dailyTask = null;
                                }

                                loadTodayCompletedTask(
                                        userMessage
                                );
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<Task> call,
                                    @NonNull Throwable t
                            ) {

                                dailyTask = null;

                                loadTodayCompletedTask(
                                        userMessage
                                );
                            }
                        }
                );
    }

    private void loadTodayCompletedTask(
            String userMessage) {

        apiInterface
                .getTodayCompletedTask(
                        "Bearer " + authToken
                )
                .enqueue(
                        new Callback<CompletedTask>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<CompletedTask> call,
                                    @NonNull Response<CompletedTask> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    todayCompletedTask =
                                            response.body();

                                } else {

                                    todayCompletedTask = null;
                                }

                                sendMessageToGemini(
                                        userMessage
                                );
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<CompletedTask> call,
                                    @NonNull Throwable t
                            ) {

                                todayCompletedTask = null;

                                sendMessageToGemini(
                                        userMessage
                                );
                            }
                        }
                );
    }

    private void sendMessageToGemini(
            String userMessage) {

        String context =
                buildStepCalContext();

        String fullMessage =
                "You are StepCal Coach, an AI assistant inside the StepCal fitness app.\n\n"
                        + "You can answer general questions normally.\n"
                        + "When the user's question is related to their StepCal data, use the StepCal context below.\n"
                        + "Do not invent StepCal data that is not provided.\n"
                        + "If a piece of StepCal data is unavailable, say that it is unavailable.\n\n"
                        + "STEP CAL CONTEXT:\n"
                        + context
                        + "\n\nUSER MESSAGE:\n"
                        + userMessage;

        Map<String, String> request =
                new HashMap<>();

        request.put(
                "message",
                fullMessage
        );

        replaceLastCoachMessage(
                "Thinking..."
        );

        apiInterface
                .sendCoachMessage(request)
                .enqueue(
                        new Callback<Map<String, String>>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<Map<String, String>> call,
                                    @NonNull Response<Map<String, String>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    String coachResponse =
                                            response.body()
                                                    .get("response");

                                    if (coachResponse != null
                                            && !coachResponse
                                            .trim()
                                            .isEmpty()) {

                                        replaceLastCoachMessage(
                                                coachResponse
                                        );

                                    } else {

                                        replaceLastCoachMessage(
                                                "I couldn't generate a response right now. Please try again."
                                        );
                                    }

                                } else {

                                    replaceLastCoachMessage(
                                            "StepCal Coach couldn't connect right now. Please try again."
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<Map<String, String>> call,
                                    @NonNull Throwable t
                            ) {

                                replaceLastCoachMessage(
                                        "I couldn't reach StepCal Coach. Please check that the backend is running and try again."
                                );
                            }
                        }
                );
    }

    private String buildStepCalContext() {

        StringBuilder context =
                new StringBuilder();

        if (userProfile != null) {

            String firstName =
                    userProfile.getFirst_name();

            String lastName =
                    userProfile.getLast_name();

            String fullName =
                    ((firstName == null ? "" : firstName)
                            + " "
                            + (lastName == null ? "" : lastName))
                            .trim();

            context.append(
                    "Name: "
            ).append(
                    fullName.isEmpty()
                            ? "Not available"
                            : fullName
            ).append("\n");

            context.append(
                    "Age: "
            ).append(
                    userProfile.getAge()
            ).append("\n");

            context.append(
                    "Height: "
            ).append(
                    userProfile.getHeight()
            ).append("\n");

            context.append(
                    "Weight: "
            ).append(
                    userProfile.getWeight()
            ).append("\n");

            context.append(
                    "Gender: "
            ).append(
                    safeValue(
                            userProfile.getGender()
                    )
            ).append("\n");

            context.append(
                    "Goal: "
            ).append(
                    safeValue(
                            userProfile.getGoal()
                    )
            ).append("\n");

            context.append(
                    "Activity level: "
            ).append(
                    safeValue(
                            userProfile.getActivity_level()
                    )
            ).append("\n");

        } else {

            context.append(
                    "Profile data: unavailable\n"
            );
        }

        if (dailyTask != null) {

            context.append(
                    "Daily calorie target: "
            ).append(
                    dailyTask.getTarget_Calorie()
            ).append(
                    " kcal\n"
            );

            Map<String, Double> exercises =
                    dailyTask.getExercises();

            if (exercises != null
                    && !exercises.isEmpty()) {

                context.append(
                        "Recommended exercises: "
                ).append(
                        exercises
                ).append("\n");

            } else {

                context.append(
                        "Recommended exercises: unavailable\n"
                );
            }

        } else {

            context.append(
                    "Daily task data: unavailable\n"
            );
        }

        if (todayCompletedTask != null) {

            double foodCalories =
                    todayCompletedTask
                            .getCalorie_intake();

            double workoutCalories =
                    todayCompletedTask
                            .getCalorie_burn();

            context.append(
                    "Today's food calories: "
            ).append(
                    foodCalories
            ).append(
                    " kcal\n"
            );

            context.append(
                    "Today's workout calories burned: "
            ).append(
                    workoutCalories
            ).append(
                    " kcal\n"
            );

            context.append(
                    "Food logged today: "
            ).append(
                    foodCalories > 0
            ).append("\n");

            context.append(
                    "Workout completed today: "
            ).append(
                    workoutCalories > 0
            ).append("\n");

        } else {

            context.append(
                    "Today's completed task data: unavailable\n"
            );
        }

        return context.toString();
    }

    private String safeValue(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "Not available";
        }

        return value;
    }

    private void hideSuggestions() {

        if (suggestionsSection != null) {

            suggestionsSection.setVisibility(
                    View.GONE
            );
        }
    }

    private void addUserMessage(
            String message) {

        LinearLayout row =
                new LinearLayout(this);

        row.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        row.setGravity(
                Gravity.END
        );

        row.setPadding(
                0,
                dpToPx(5),
                0,
                dpToPx(5)
        );

        TextView bubble =
                new TextView(this);

        bubble.setText(
                message
        );

        bubble.setTextColor(
                Color.rgb(74, 23, 76)
        );

        bubble.setTextSize(
                16
        );

        bubble.setTypeface(
                Typeface.DEFAULT,
                Typeface.NORMAL
        );

        bubble.setGravity(
                Gravity.CENTER_VERTICAL
        );

        bubble.setLineSpacing(
                dpToPx(1),
                1.0f
        );

        bubble.setPadding(
                dpToPx(16),
                dpToPx(12),
                dpToPx(16),
                dpToPx(12)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(238, 225, 247)
        );

        background.setCornerRadius(
                dpToPx(20)
        );

        background.setStroke(
                dpToPx(1),
                Color.rgb(222, 204, 234)
        );

        bubble.setBackground(
                background
        );

        bubble.setElevation(
                dpToPx(2)
        );

        LinearLayout.LayoutParams bubbleParams =
                new LinearLayout.LayoutParams(
                        dpToPx(285),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        bubbleParams.setMargins(
                dpToPx(8),
                0,
                0,
                0
        );

        row.addView(
                bubble,
                bubbleParams
        );

        chatContainer.addView(
                row
        );

        scrollToBottom();
    }

    private void addCoachMessage(
            String message) {

        LinearLayout row =
                new LinearLayout(this);

        row.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        row.setGravity(
                Gravity.START
        );

        row.setPadding(
                0,
                dpToPx(5),
                0,
                dpToPx(5)
        );

        TextView bubble =
                new TextView(this);

        bubble.setText(
                message
        );

        bubble.setTextColor(
                Color.rgb(70, 49, 48)
        );

        bubble.setTextSize(
                16
        );

        bubble.setTypeface(
                Typeface.DEFAULT,
                Typeface.NORMAL
        );

        bubble.setGravity(
                Gravity.CENTER_VERTICAL
        );

        bubble.setLineSpacing(
                dpToPx(1),
                1.0f
        );

        bubble.setPadding(
                dpToPx(16),
                dpToPx(12),
                dpToPx(16),
                dpToPx(12)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(255, 234, 224)
        );

        background.setCornerRadius(
                dpToPx(20)
        );

        background.setStroke(
                dpToPx(1),
                Color.rgb(245, 211, 198)
        );

        bubble.setBackground(
                background
        );

        bubble.setElevation(
                dpToPx(2)
        );

        LinearLayout.LayoutParams bubbleParams =
                new LinearLayout.LayoutParams(
                        dpToPx(285),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        bubbleParams.setMargins(
                0,
                0,
                dpToPx(8),
                0
        );

        row.addView(
                bubble,
                bubbleParams
        );

        chatContainer.addView(
                row
        );

        scrollToBottom();
    }

    private void replaceLastCoachMessage(
            String message) {

        if (chatContainer.getChildCount() == 0) {

            addCoachMessage(
                    message
            );

            return;
        }

        View lastView =
                chatContainer.getChildAt(
                        chatContainer.getChildCount() - 1
                );

        if (!(lastView instanceof LinearLayout)) {

            addCoachMessage(
                    message
            );

            return;
        }

        LinearLayout row =
                (LinearLayout) lastView;

        if (row.getChildCount() == 0) {

            addCoachMessage(
                    message
            );

            return;
        }

        View bubbleView =
                row.getChildAt(0);

        if (bubbleView instanceof TextView) {

            TextView bubble =
                    (TextView) bubbleView;

            bubble.setText(
                    message
            );

            scrollToBottom();

        } else {

            addCoachMessage(
                    message
            );
        }
    }

    private void scrollToBottom() {

        chatScroll.post(
                new Runnable() {
                    @Override
                    public void run() {

                        chatScroll.fullScroll(
                                View.FOCUS_DOWN
                        );
                    }
                }
        );
    }

    private int dpToPx(
            int dp) {

        return (int) (
                dp *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}