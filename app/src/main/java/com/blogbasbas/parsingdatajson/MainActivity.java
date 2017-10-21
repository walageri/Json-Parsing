package com.blogbasbas.parsingdatajson;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.blogbasbas.parsingdatajson.model.ModelCountry;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int DEBUG = 1;

    CountryAdapter adapter;
    ListView lsCountry;
    Context context = this;
    AQuery aQuery;
    ArrayList<ModelCountry> dataCountry;
    //http://www.androidbegin.com/tutorial/jsonparsetutorial.txt
    public static final String BASE_URL = "http://www.androidbegin.com/tutorial/jsonparsetutorial.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lsCountry = (ListView)findViewById(R.id.listview);

        aQuery = new AQuery(context);

        dataCountry = new ArrayList<>();

        getDataCountry();
        lsCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // ModelCountry m = new ModelCountry();
                Intent kirimData = new Intent(MainActivity.this, DetailActivity.class);
                kirimData.putExtra("rank",dataCountry.get(i).getRank());
                kirimData.putExtra("country",dataCountry.get(i).getCountry());
                kirimData.putExtra("population",dataCountry.get(i).getPopulation());
                kirimData.putExtra("flag",dataCountry.get(i).getFlag());
                startActivity(kirimData);

            }
        });
    }

    public void getDataCountry(){
        String url = BASE_URL;
        Map<String, String> paramps = new HashMap<>();
        //menambahkan progress dialog

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("Loading");

        try{
            pre("url : " + url + ", params " + paramps.toString());
            aQuery.progress(dialog).ajax(url, paramps, String.class, new AjaxCallback<String>(){
                @Override
                public void callback(String url, String hasil, AjaxStatus status){
                    if (hasil != null){
                        try{
                            JSONObject jsonObject = new JSONObject(hasil);
//                           String result = jsonObject.getString("result");

                            JSONArray jsonArray = jsonObject.getJSONArray("worldpopulation");
                            for (int a = 0 ; a < jsonArray.length(); a++){
                                JSONObject object = jsonArray.getJSONObject(a);
                                ModelCountry modelCountry = new ModelCountry();
                                modelCountry.setRank(object.getString("rank"));
                                modelCountry.setCountry(object.getString("country"));
                                modelCountry.setPopulation(object.getString("population"));
                                modelCountry.setFlag(object.getString("flag"));

                                dataCountry.add(modelCountry);
                                adapter = new CountryAdapter(context, dataCountry);
                                lsCountry.setAdapter(adapter);



                            }


                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error Parsing Data", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error get  Data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private class CountryAdapter extends BaseAdapter {

        private Context c;
        private ArrayList<ModelCountry>countries;
        private LayoutInflater inflater = null;

        public CountryAdapter(Context c, ArrayList<ModelCountry> country){
            this.c = c;
            countries = new ArrayList<>();
            this.countries = country;
        }


        @Override
        public int getCount() {
            return countries.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        private class ViewHolder{
            TextView itemRank, itemPopulation, itemCountry;
            ImageView imgFlag;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ViewHolder holder ;
            if(v==null){
                inflater = (LayoutInflater)c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item ,null);
                holder = new ViewHolder();
                holder.itemRank = (TextView)v.findViewById(R.id.tvRankItem);
                holder.itemCountry = (TextView)v.findViewById(R.id.tvCountryItem);
                holder.itemPopulation = (TextView)v.findViewById(R.id.tvPopulationItem);
                holder.imgFlag = (ImageView)v.findViewById(R.id.imgList);

            }else{
                holder = (ViewHolder)v.getTag();
            }

            ModelCountry modelCountry = countries.get(i);
            //menampilkan data ke widget
            holder.itemCountry.setText(modelCountry.getCountry());
            holder.itemRank.setText(modelCountry.getRank());
            holder.itemPopulation.setText(modelCountry.getPopulation());
            Picasso.with(getApplicationContext()).load(modelCountry.getFlag()).placeholder(R.drawable.imgno).
                    into(holder.imgFlag);
            return v;
        }
    }

    public static void pre(String pesan) {
        try {
            if (DEBUG == 1) {
                System.out.println(pesan);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}