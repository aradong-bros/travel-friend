package com.example.estsoft.travelfriendflow2.sale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.mytravel.SelectedCityActivity;
import com.example.estsoft.travelfriendflow2.mytravel.Travel;
import com.facebook.internal.ImageDownloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Handler;

public class SaleActivity extends Activity {

    private static String url = "http://222.239.250.207:8080/TravelFriendAndroid/saleInfo/getSaleInfoListByCity";       //   +  /{cityList_no 값}
    private static String LOG_TAG = "SaleActivity";
    ArrayList<SaleItem> saleItems = new ArrayList<SaleItem>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<Bitmap>();
    private ListView lv;
    private SaleItemAdapter saleItemAdapter;
    Bitmap bitmap;


    private ImageView mProgressBar;
    private LinearLayout loading_layout;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);


        LayoutInflater inflater = getLayoutInflater();
        loading_layout = (LinearLayout)inflater.inflate(R.layout.custom_progressbar,null);
        addContentView(loading_layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        loading_layout.setVisibility(View.GONE);


        mProgressBar = (ImageView)loading_layout.findViewById(R.id.imageview_custom_progress);
        mProgressBar.setVisibility(View.GONE);

        animationDrawable = (AnimationDrawable)mProgressBar.getDrawable();
        //animationDrawable.start();



        //스피너 하나
        String[] option = getResources().getStringArray(R.array.spinnerArray1);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, option);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int cityList_no = position + 1;

                loading_layout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                animationDrawable.start();

                new HttpParamConnThread().execute(url, cityList_no + "");
            } // HttpParamConnThread 클래스의 doInBackground 로 이동

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        saleItemAdapter = new SaleItemAdapter(getApplicationContext(), R.layout.saleitem, saleItems);
        lv = (ListView) findViewById(R.id.salelist);
        /** 리스트뷰에 어댑터 객체 설정*/
        lv.setAdapter(saleItemAdapter);

        /** 리스트뷰의 각 리스트 터치시 이동URL 설정*/
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String title = saleItems.get(position).getTitle();
                String image = saleItems.get(position).getImage();
                String link = saleItems.get(position).getLink();

                /** 액티비티 시작시 URL로 이동*/
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                intent.putExtra("title", title);
                intent.putExtra("image", image);
                intent.putExtra("link", link);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindDrawables(lv);
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view == null)
            return;

        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(null);
        }
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
            //((BitmapDrawable)view.getBackground()).getBitmap().recycle(); /* 재사용할꺼면 사용XXX */
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            view.setBackgroundResource(0);
            view.setBackgroundDrawable(null);
        }
    }


    public class HttpParamConnThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... path) {
            // URL 연결이 구현될 부분
            URL url;
            String response = "";
            String CONNURL = path[0];
            String VALUE = path[1];
            HttpURLConnection conn = null;
            try {

                url = new URL(CONNURL + "/" + VALUE);
                conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                Log.e("http response code", responseCode + "");

                if (responseCode == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("RESPONSE", "The response is: " + response);
                    return response;
                    // onPostExecute 메소드로 response 전달

                } else {
                    Log.e(LOG_TAG, "서버 접속 실패");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                        }
                    });
                    //  로딩바 띄우기
                }

            } catch (ConnectTimeoutException ue) {
                Log.e(LOG_TAG, "ConnectTimeoutException");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            new Thread(new Runnable() { public void run() {
                if (("").equals(result) || TextUtils.isEmpty(result)) {
                    //  로딩바 띄우기
                    Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (parsePinData(result)) {

                    bitmapArrayList.clear();

                    for ( int i = 0; i < saleItems.size(); i++ )
                    {
                        getBitmap(saleItems.get(i));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showResult();
                            loading_layout.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                            animationDrawable.stop();
                        }
                    });

                }
            }}).start();


        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

    }   // End_HttpParamConnThread

    //
    public void getBitmap(SaleItem saleItem)
    {
        final String imageURL = saleItem.getImage();

        /**
         * 안드로이드에서 네트워크 관련 작업을 할 때는
         * 반드시 메인 스레드가 아닌 별도의 작업 스레드에서 작업해야 합니다.
         */

        Thread mThread = new Thread() {

            @Override
            public void run() {
                try {
                    /** URL 주소를 이용해서 URL 객체 생성 */
                    URL url = new URL(imageURL);

                    /**
                     * 아래 코드는 웹에서 이미지를 가져온 뒤
                     * 이미지 뷰에 지정할 Bitmap을 생성하는 과정
                     * */
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, true);

                    bitmap = Bitmap.createBitmap(bitmap
                            , bitmap.getWidth() / 4 //X 시작위치 (원본의 10/1지점)
                            , bitmap.getHeight() / 4 //Y 시작위치 (원본의 10/1지점)
                            , bitmap.getWidth() / 2 // 넓이 (원본의 절반 크기)
                            , bitmap.getHeight() / 2); // 높이 (원본의 절반 크기)

                } catch (IOException ex) {

                }
            }
        };

        /** 웹에서 이미지를 가져오는 작업 스레드 실행 */
        mThread.start();

        try {
            /**
             * 메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
             * 대기해야 하므로 작업스레드의 join() 메소드를 호출해서
             * 메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.
             * */
            mThread.join();
            bitmapArrayList.add(bitmap);
        } catch (InterruptedException e) {
            Log.e("ImageThread of SaleInfo", "이미지쓰레드 에러 발생");
        }
    }

    protected boolean parsePinData(String myJSON) {

        saleItems.clear();

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray datas = jsonObj.getJSONArray("saleInfoListByCity");

            for (int i = 0; i < datas.length(); i++) {
                JSONObject object = datas.getJSONObject(i);

                SaleItem saleItem = new SaleItem(object.getInt("no"),
                        object.getInt("cityList_no"),
                        object.getString("title"),
                        object.getString("image"),
                        object.getString("link"));

                saleItems.add(saleItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }// End_parsePinData

    //UI 처리는 이곳에서 한다.
    private boolean showResult() {
        saleItemAdapter.notifyDataSetChanged();
        return true;
    }

    class SaleItemAdapter extends BaseAdapter {
        Context context;
        int layout;
        ArrayList<SaleItem> saleItems;
        LayoutInflater inf;

        private ViewHolder viewHolder = null;

        public SaleItemAdapter(Context context, int layout, ArrayList<SaleItem> saleItems) {
            this.context = context;
            this.layout = layout;
            this.saleItems = saleItems;
            this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return saleItems.size();
        }

        @Override
        public Object getItem(int position) {
            return saleItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.saleitem, parent, false);
//            convertView = inf.inflate(layout, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final TextView title = (TextView) convertView.findViewById(R.id.SaleTextBox);
            final ImageView image = (ImageView) convertView.findViewById(R.id.SaleImage);

            final SaleItem t = saleItems.get(position);

            // 할인정보 리스트뷰에 타이틀 넣기
            title.setText(t.getTitle());
            // 할인정보 리스트뷰에 이미지 넣기
            image.setImageBitmap(bitmapArrayList.get(position));

            return convertView;
        }
    }

    class ViewHolder {
        ImageView image;
        TextView title;

        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.SaleImage);
            title = (TextView) view.findViewById(R.id.SaleTextBox);
        }
    }

}

class SaleItem{
    int no = 0;
    int cityList_no = 0;
    String title = "";
    String image = "";
    String link = "";

    public SaleItem(){}

    public SaleItem(int no, int cityList_no, String title, String image, String link){
        this.no = no;
        this.cityList_no = cityList_no;
        this.title = title;
        this.image = image;
        this.link = link;
    }
    public int getNo() {
        return no;
    }
    public int getCityList_No() {
        return cityList_no;
    }
    public String getTitle() {
        return title;
    }
    public String getImage() {
        return image;
    }
    public String getLink() {
        return link;
    }
}
