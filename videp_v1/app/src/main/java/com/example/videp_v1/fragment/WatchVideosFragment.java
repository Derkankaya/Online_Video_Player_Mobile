package com.example.videp_v1.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.videp_v1.R;


public class WatchVideosFragment extends Fragment {

    private WebView videoView;


    // YouTube API'den alınan video URL's

// ...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment'ın layout dosyasını inflate et
        View view = inflater.inflate(R.layout.fragment_watch_videos, container, false);

        // TextView nesnelerini layout dosyasındaki öğelerle eşleştir
        TextView watchWideoTitle = view.findViewById(R.id.watchWideoTitle);
        TextView watchPubhlisher = view.findViewById(R.id.watchPubhlisher);
        TextView watchDescription = view.findViewById(R.id.watchDescription);
        TextView watchViewCount = view.findViewById(R.id.watchViewCount);
        TextView watchDate = view.findViewById(R.id.watchDate);

        // Video ID ve intVideoID için başlangıç değerleri
        String strVideoID = "null";
        int intVideoID = 0;

        // getArguments()'ın null olup olmadığını kontrol et
        if (getArguments() != null) {
            // getArguments() içindeki verileri kullanarak TextView'lara değerleri atayın
            if (getArguments().containsKey("videoTitle")) {
                watchWideoTitle.setText(getArguments().getString("videoTitle"));
            }
            if (getArguments().containsKey("videoPublisher")) {
                watchPubhlisher.setText(getArguments().getString("videoPublisher"));
            }
            if (getArguments().containsKey("videoDescription")) {
                watchDescription.setText(getArguments().getString("videoDescription"));
            }
            if (getArguments().containsKey("videoViews")) {
                watchViewCount.setText(getArguments().getString("videoViews"));
            }
            if (getArguments().containsKey("videoDate")) {
                watchDate.setText(getArguments().getString("videoDate"));
            }
            if (getArguments().containsKey("videoID")) {
                // videoID değerini al
                strVideoID = getArguments().getString("videoID");
            }

            try {
                // strVideoID'yi intVideoID'ye çevirme işlemi


                    // YouTube video oynatıcı için gerekli HTML ve JavaScript kodları
                    String stringJavaScript = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "  <body>\n" +
                            "    <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->\n" +
                            "    <div id=\"player\"></div>\n" +
                            "    <script>\n" +
                            "      // 2. This code loads the IFrame Player API code asynchronously.\n" +
                            "      var tag = document.createElement('script');\n" +
                            "      tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                            "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                            "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                            "      var player;\n" +
                            "      function onYouTubeIframeAPIReady() {\n" +
                            "        player = new YT.Player('player', {\n" +
                            "          height: '390',\n" +
                            "          width: '640',\n" +
                            "          videoId: '" + strVideoID + "',\n" +
                            "          playerVars: {\n" +
                            "            'playsinline': 1\n" +
                            "          },\n" +
                            "          events: {\n" +
                            "            'onReady': onPlayerReady,\n" +
                            "            'onStateChange': onPlayerStateChange\n" +
                            "          }\n" +
                            "        });\n" +
                            "      }\n" +
                            "      function onPlayerReady(event) {\n" +
                            "        event.target.playVideo();\n" +
                            "      }\n" +
                            "      function onPlayerStateChange(event) {\n" +
                            "        if (event.data == YT.PlayerState.PLAYING && !done) {\n" +
                            "          setTimeout(stopVideo, 6000);\n" +
                            "          done = true;\n" +
                            "        }\n" +
                            "      }\n" +
                            "      function stopVideo() {\n" +
                            "        player.stopVideo();\n" +
                            "      }\n" +
                            "    </script>\n" +
                            "  </body>\n" +
                            "</html>";

                    // VideoView nesnesini layout dosyasındaki öğe ile eşleştir
                    videoView = view.findViewById(R.id.videoView);
                    videoView.getSettings().setJavaScriptEnabled(true);
                    videoView.loadData(stringJavaScript, "text/html", "utf-8");

            } catch (NumberFormatException e) {
                // Hata durumunda ekrana bir mesaj yazdır
                System.out.println("Invalid number format. Cannot convert to integer.");
            }
        }

        // ... Diğer kodlar ...

        // Fragment'ın layout dosyasını return et
        return view;
    }

// ...

    public static int convertStringToInt(String str) throws NumberFormatException {
        return Integer.parseInt(str);
    }







}

