package com.example.bit_user.myapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    private EditText etMessage;
    private Button btnSend;
    private TextView tvRecvData;

    private TextView seq1, id1, pw1, email1, phone1, age1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //통신시 필요함
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        etMessage = (EditText) findViewById(R.id.et_message);
        btnSend = (Button) findViewById(R.id.btn_sendData);
        tvRecvData = (TextView)	findViewById(R.id.tv_recvData);
        seq1 = (TextView) findViewById(R.id.seq1);
        id1 = (TextView) findViewById(R.id.id1);
        pw1 = (TextView) findViewById(R.id.pw1);
        email1 = (TextView) findViewById(R.id.email1);
        phone1 = (TextView) findViewById(R.id.phone1);
        age1 = (TextView) findViewById(R.id.age1);


        /*	Send 버튼을 눌렀을 때 서버에 데이터를 보내고 받는다	*/
        btnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String sMessage = etMessage.getText().toString(); // 보내는 메시지를 받아옴
                String result = SendByHttp(); // 메시지를 서버에 보냄
                System.out.print(result);
                String[][] parsedData = jsonParserList(result, sMessage); // JSON 데이터 파싱

               // tvRecvData.setText(result);	// 받은 메시지를 화면에 보여주기

            }
        });
    }

    /**
     * 서버에 데이터를 보내는 메소드
     * @param msg
     * @return
     */
    private String SendByHttp() {
        String URL = "http://192.168.1.6:9090/xmlparshing/userInfo.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {
			/* 체크할 id와 pwd값 서버로 전송 */
            HttpPost post = new HttpPost(URL);

			/* 지연시간 최대 5초 */
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader( new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            client.getConnectionManager().shutdown();	// 연결 지연 종료
            return "";
        }
    }

    /**
     * 받은 JSON 객체를 파싱하는 메소드
     * @param page
     * @return
     */
    private String[][] jsonParserList(String pRecvServerPage, String id) {

        Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("sendData");

            // 받아온 pRecvServerPage를 분석하는 부분
            String[] jsonName = {"PASSWORD", "PHONE", "JOINDATE","USER_ID","EMAIL","SEQ","AGE"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);

                for(int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }

            // 분해 된 데이터를 확인하기 위한 부분
            for(int i=0; i<parseredData.length; i++){

                if(String.valueOf(parseredData[i][3]).equals(id)){
                    Log.i("정답 "+i+" : ", parseredData[i][0]);
                    seq1.setText("    "+parseredData[i][5]);
                    id1.setText("    "+parseredData[i][3]);
                    pw1.setText("    "+parseredData[i][0]);
                    email1.setText("    "+parseredData[i][4]);
                    phone1.setText("    "+parseredData[i][1]);
                    age1.setText("    "+parseredData[i][6]);
                }
                        Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][0]);
                        Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][1]);
                        Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][2]);
                        Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][3]);
                        Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][4]);
                        Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][5]);
                        Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][6]);
            }

            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
