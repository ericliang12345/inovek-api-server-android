package bms.device.webapi.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import bms.device.webapi.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FunctionTestActivity extends Activity {

    /* UI components */
    EditText editName, editPassword;
    Button loginButton, helloButton, changePasswordButton, deletePasswordButton;
    Button scanResultsButton, getWiFiNetworkButton, postWiFiNetworkButton;

    AlertDialog alertDialog = null;

    /* http session */
    OkHttpClient okHttpClient = new OkHttpClient();
    String accessToken = null;
    String refreshToken = null;

    /* url */
    String baseUrl = "http://127.0.0.1:8080/v1";
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_test);

        /* init UI components */
        editName = findViewById(R.id.edit_name);
        editPassword = findViewById(R.id.edit_password);

        loginButton = findViewById(R.id.test_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(true);
            }
        });

        helloButton = findViewById(R.id.test_hello_button);
        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hello();
            }
        });

        changePasswordButton = findViewById(R.id.test_change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        deletePasswordButton = findViewById(R.id.test_delete_password_button);
        deletePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePassword();
            }
        });

        scanResultsButton = findViewById(R.id.test_scan_result_button);
        scanResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanResults();
            }
        });

        getWiFiNetworkButton = findViewById(R.id.test_get_wifi_network_button);
        getWiFiNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWiFiNetwork();
            }
        });

        postWiFiNetworkButton = findViewById(R.id.test_post_wifi_network_button);
        postWiFiNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postWiFiNetwork();
            }
        });

        /* init alert dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Result");
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void showMessageAsync(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setMessage(message);
                alertDialog.show();
            }
        });
    }

    private void showMessageAsync(Response response) {
        String msg = response.code() + " " + response.message();
        try {
            String body = response.body().string();
            if (body.length() > 0) {
                msg += "\n" + parse(body);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String message = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setMessage(message);
                alertDialog.show();
            }
        });
    }

    private String parse(String json) {
        Map<String, String> map = new HashMap<>();

        String[] lines = json.replace("{", "")
                .replace("}", "")
                .replace("\":", " = ")
                .replace("\"", "")
                .split(",");

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }

        return sb.toString();
    }

    private void get(String uri, boolean authRequired) {
        Request.Builder builder = new Request.Builder();
        builder.url(baseUrl + uri);
        if (authRequired) {
            if (accessToken != null) {
                builder.header("Authorization", "Bearer " + accessToken);
            } else {
                alertDialog.setMessage("Please login first");
                alertDialog.show();
                return;
            }
        }
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showMessageAsync(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                showMessageAsync(response);
            }
        });
    }

    private void post(String uri, String json, boolean authRequired) {
        Request.Builder builder = new Request.Builder();
        builder.url(baseUrl + uri);
        if (authRequired) {
            if (accessToken != null) {
                builder.header("Authorization", "Bearer " + accessToken);
            } else {
                alertDialog.setMessage("Please login first");
                alertDialog.show();
                return;
            }
        }
        builder.post(RequestBody.create(JSON, json));
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showMessageAsync(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                showMessageAsync(response);
            }
        });
    }

    private void login(final boolean showResult) {
        String name = editName.getText().toString();
        String password = editPassword.getText().toString();

        String jsonFormat = "{ 'grant_type':'password', 'username':'%s', 'password':'%s' }";
        String json = String.format(jsonFormat, name, password);

        Request request = new Request.Builder()
                .url(baseUrl + "/oauth2/token")
                .post(RequestBody.create(JSON, json))
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                String message = response.code() + " " + response.message();;
                try {
                    String body = response.body().string();
                    if (body.length() > 0) {
                        message = parse(body);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (showResult) {
                    showMessageAsync(message);
                }

                Properties properties = new Properties();
                try {
                    properties.load(new StringReader(message));
                    accessToken = properties.getProperty("access_token");
                    refreshToken = properties.getProperty("refresh_token");
                } catch (Exception e) {
                    accessToken = null;
                    refreshToken = null;
                    e.printStackTrace();
                }
            }
        });
    }

    private void hello() {
        String name = editName.getText().toString();

        get("/hello/" + name, false);
    }

    private void changePassword() {
        String name = editName.getText().toString();
        String password = editPassword.getText().toString();
        String json = "{ \"value\": \"" + password + "\" }";

        post("/user/password", json, true);
    }

    private void deletePassword() {
        Request.Builder builder = new Request.Builder();
        builder.url(baseUrl + "/user/password");

        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        } else {
            alertDialog.setMessage("Please login first");
            alertDialog.show();
            return;
        }
        builder.delete();
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showMessageAsync(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                showMessageAsync(response);
            }
        });
    }

    private void scanResults() {
        get("/wifi/scan_results", true);
    }

    private void getWiFiNetwork() {
        get("/wifi/network", true);
    }

    private void postWiFiNetwork() {

    }

}
