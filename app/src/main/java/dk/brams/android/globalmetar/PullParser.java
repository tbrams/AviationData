package dk.brams.android.globalmetar;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class PullParser {
    private static final String TAG = "PullParser";

    public static ArrayList<String> parse(String xml) {
        ArrayList<String> results = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("raw_text".equals(xpp.getName())) {
                        xpp.next();
                        results.add(xpp.getText());
                    }
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return results;
    }
}
