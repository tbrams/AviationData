package dk.brams.android.globalmetar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class DataFragment extends Fragment {

    private static final String TAG = "DataFragment";

    private Button metarButton, tafButton, notamButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buttons, container, false);

        metarButton = (Button) v.findViewById(R.id.metarButton);
        metarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "starting metar task");
                new FetchMetarTask().execute();
            }
        });

        tafButton = (Button) v.findViewById(R.id.tafButton);
        tafButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "starting taf task");
                new FetchTafTask().execute();
            }
        });

        notamButton = (Button) v.findViewById(R.id.notamButton);
        notamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "starting notam task");
                new FetchNotamsTask().execute();
            }
        });

        return v;
    }


    private class FetchNotamsTask extends AsyncTask<Void, Void, Void> {
        public static final String TAG = "FetchNotamsTask";
        private static final String website = "https://pilotweb.nas.faa.gov/PilotWeb/notamRetrievalByICAOAction.do?method=displayByICAOs&retrieveLocId=EKRK&reportType=RAW&formatType=ICAO&actionType=notamRetrievalByICAOs";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc;
                Connection.Response res = Jsoup.connect(website).execute();

                doc = res.parse();
                Elements notams = doc.select("div#resultsHomeLeft");
                // Skip the first two elements and focus on each individual notam
                for (int i = 2; i < notams.size(); i++) {
                    Log.i(TAG, "Notam #" +(i-1)+" : "+ notams.get(i).select("div#notamRight").text());
                }

            } catch (
                    IOException ioe)

            {
                Log.d(TAG, "Failed to fetch URL: " + website);
            }

            return null;

        }
    }

    private class FetchMetarTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "FetchMetarTask";
        private final String website = "https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString=EKCH&hoursBeforeNow=1";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String result = new ByteFetcher().getUrlString(website);
                // Log.i(TAG, "Fetched content of URL: " + result);

                ArrayList<String> metars = new PullParser().parse(result);
                for (String metar : metars) {
                    Log.i(TAG, "<METAR>: " + metar);
                }
            } catch (IOException ioe) {
                Log.d(TAG, "Failed to fetch URL: "+ website);
            }
            return null;
        }

    }


    private class FetchTafTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "FetchTafTask";
        private final String website = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=tafs&requestType=retrieve&format=xml&stationString=EKRK,EKOD&hoursBeforeNow=3&timeType=issue&mostRecent=true";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String result = new ByteFetcher().getUrlString(website);

                ArrayList<String> tafs = new PullParser().parse(result);
                for (String taf : tafs) {
                    Log.i(TAG, "<TAF>: " + taf);
                }
            } catch (IOException ioe) {
                Log.d(TAG, "Failed to fetch URL: "+ website);
            }
            return null;
        }

    }
}

