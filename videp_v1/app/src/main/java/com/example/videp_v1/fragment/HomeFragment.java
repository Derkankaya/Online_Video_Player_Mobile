package com.example.videp_v1.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videp_v1.R;
import com.example.videp_v1.adapter.ContentAdapter;
import com.example.videp_v1.model.ContentModel;
import com.example.videp_v1.users.GoogleUsersClass;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class HomeFragment extends Fragment implements ContentAdapter.ItemClickListener {

    private static final String GOOGLE_YOUTUBE_API_KEY = "AIzaSyBxbb-xKGcjUBG0_rzTU5sAjnmskrIYqHM";
    private static final String GET_POPULAR_VIDEOS_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&chart=mostPopular&regionCode=US&maxResults=10&videoCategoryId=0&key=" + GOOGLE_YOUTUBE_API_KEY;

    private FirebaseAuth auth;
    private GoogleUsersClass googleUsersClass;
    private RecyclerView recyclerView;
    private ContentAdapter adapter;
    private ArrayList<ContentModel> videoList;
    public boolean isVideoListFetchedMain = false;

    private static final String ACCESS_TOKEN = "USER_ACCESS_TOKEN";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        googleUsersClass = new GoogleUsersClass(getActivity(), auth);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        yourMethodWhereYouCallRefresh();




        // Video listesi daha önce alınmış mı kontrolü




        return view;
    }

    @Override
    public void onItemClick(ContentModel contentModel) {
        // Tıklanan videoya ait verileri kullanarak yeni bir fragment başlat
        WatchVideosFragment watchVideosFragment = new WatchVideosFragment();

        // Tıklanan videoya ait verileri WatchVideoFragment'e iletebilirsiniz.
        Bundle bundle = new Bundle();
     /*   if (!isVideoListFetched) {

            isVideoListFetched = true;
        }*/
        bundle.putString("videoUrl", contentModel.getVideoUrl());
        bundle.putString("videoTitle", contentModel.getVideo_title());
        bundle.putString("videoPublisher", contentModel.getPublisher());
        bundle.putString("videoDescription", contentModel.getVideo_description());
        bundle.putString("videoViews", contentModel.getViews());
        bundle.putString("videoDate", contentModel.getDate());
        bundle.putString("videoID", contentModel.getId());
        watchVideosFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.item_video, watchVideosFragment); // Doğru id'yi kullanın
        transaction.addToBackStack(null);
        transaction.commit();

        Log.d("HomeFragment", "onItemClick called. Video Title: " + contentModel.getVideo_title() + ", Video URL: " + contentModel.getVideoUrl());
    }
    public void yourMethodWhereYouCallRefresh() {

        refreshHomeFragment(isVideoListFetchedMain);

            isVideoListFetchedMain = true;

    }
    public void refreshHomeFragment(boolean isVideoListFetchedMain) {
        if (!isVideoListFetchedMain) {
            if (googleUsersClass.hasGoogleAuthenticator()) {
                new YouTubeApiHelper().execute();
                isVideoListFetchedMain = true;
            } else {
                new RequestYoutubeAPI().execute();
            }

        }
    }


    @Override
    public ArrayList<ContentModel> doInBackground(Void... voids) {
        return null;
    }

    @Override
    public void onPostExecute(ArrayList<ContentModel> videoList) {

    }


    private class RequestYoutubeAPI extends AsyncTask<Void, Void, ArrayList<ContentModel>> {
        @Override
        protected ArrayList<ContentModel> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json; charset=utf-8")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            Request request = new Request.Builder()
                    .url(GET_POPULAR_VIDEOS_URL)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = new String(response.body().bytes(), StandardCharsets.UTF_8);
                Log.d("HomeFragment", "API Response: " + responseBody);
                return parseVideoList(responseBody);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ContentModel> videoList) {
            super.onPostExecute(videoList);

            if (isAdded() && getActivity() != null) {
                if (videoList != null) {
                    displayVideos(videoList);
                } else {
                    Log.e("HomeFragment", "Video listesi alınamadı");
                    Toast.makeText(getActivity(), "Video listesi alınamadı", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void displayVideos(ArrayList<ContentModel> videoList) {
            Collections.shuffle(videoList);
            adapter = new ContentAdapter(getActivity(), videoList, HomeFragment.this);
            recyclerView.setAdapter(adapter);
        }

        private ArrayList<ContentModel> parseVideoList(String responseBody) throws JSONException {
            ArrayList<ContentModel> videoList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                JSONObject snippet = item.getJSONObject("snippet");
                String videoId = item.getString("id");
                String publisher = snippet.getString("channelTitle");
                String description = snippet.getString("description");
                String videoUrl = "http://www.youtube.com/watch?v=" + videoId;
                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                JSONObject defaultThumbnail = thumbnails.getJSONObject("default");
                String thumbnailUrl = defaultThumbnail.getString("url");

                String views = snippet.has("statistics") ? snippet.getJSONObject("statistics").getString("viewCount") : "0";
                String title = snippet.getString("title");
                String date = snippet.getString("publishedAt");

                ContentModel video = new ContentModel(videoId, publisher, description, videoUrl, thumbnailUrl, views, title, date);
                videoList.add(video);
            }

            return videoList;
        }
    }


    private class YouTubeApiHelper extends AsyncTask<Void, Void, ArrayList<ContentModel>> {
        @Override
        protected ArrayList<ContentModel> doInBackground(Void... voids) {
            try {
                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), null)
                        .setApplicationName("videp_v1")
                        .build();

                List<Playlist> playlists = getUserPlaylists(youtube);

                if (playlists != null && !playlists.isEmpty()) {
                    ArrayList<ContentModel> videoList = new ArrayList<>();
                    for (Playlist playlist : playlists) {
                        videoList.addAll(getPlaylistVideos(youtube, playlist.getId()));
                    }

                    return videoList;
                } else {
                    Log.e("YouTubeApiHelper", "Kullanıcının oynatma listesi bulunamadı.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("YouTubeApiHelper", "YouTube API ile ilgili bir hata oluştu: " + e.getMessage());
            }

            return null;
        }

        private List<Playlist> getUserPlaylists(YouTube youtube) throws IOException {
            YouTube.Playlists.List playlistRequest = youtube.playlists().list(Collections.singletonList("snippet,contentDetails"));
            playlistRequest.setMine(true);
            PlaylistListResponse playlistListResponse = playlistRequest.execute();
            return playlistListResponse.getItems();
        }

        private List<ContentModel> getPlaylistVideos(YouTube youtube, String playlistId) throws IOException {
            List<ContentModel> playlistVideos = new ArrayList<>();

            YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems()
                    .list(Collections.singletonList("snippet,contentDetails"));
            playlistItemsRequest.setPlaylistId(playlistId);
            playlistItemsRequest.setMaxResults(50L); // Örnek olarak maksimum 50 video çekildi, bu değeri ihtiyacınıza göre ayarlayabilirsiniz.

            String nextPageToken = null;
            do {
                playlistItemsRequest.setPageToken(nextPageToken);
                PlaylistItemListResponse playlistItemListResponse = playlistItemsRequest.execute();

                List<PlaylistItem> playlistItems = playlistItemListResponse.getItems();
                if (playlistItems != null) {
                    for (PlaylistItem playlistItem : playlistItems) {
                        // Video bilgilerini al
                        PlaylistItemSnippet videoSnippet = playlistItem.getSnippet();
                        String userVideoId = videoSnippet.getResourceId().getVideoId();
                        String userVideoPublisher = videoSnippet.getChannelTitle();
                        String userDescription = videoSnippet.getDescription();
                        String userVideoUrl = "http://www.youtube.com/watch?v=" + userVideoId; // Doğru URL'yi ekleyin
                        String userThumbnailUrl = videoSnippet.getThumbnails().getDefault().getUrl(); // Doğru thumbnail URL'sini ekleyin
                        String userViews = "Number_of_views"; // Doğru izlenme sayısını ekleyin
                        String userTitle = videoSnippet.getTitle();
                        String userDate = videoSnippet.getPublishedAt().toString();

                        // Video modelini oluştur
                        ContentModel video = new ContentModel(userVideoId, userVideoPublisher, userDescription,
                                userVideoUrl, userThumbnailUrl, userViews, userTitle, userDate);
                        videoList.add(video);

                        // Listeye ekle
                        playlistVideos.add(video);
                    }

                }


                nextPageToken = playlistItemListResponse.getNextPageToken();
            } while (nextPageToken != null);

            return playlistVideos;
        }


        @Override
        protected void onPostExecute(ArrayList<ContentModel> videoList) {
            super.onPostExecute(videoList);

            if (isAdded() && getActivity() != null) {
                if (videoList != null) {
                    // Video listesi başarıyla alındı, burada istediğiniz işlemleri gerçekleştirebilirsiniz.
                    for (ContentModel video : videoList) {
                        // Video listesi içindeki her bir video ile yapmak istediğiniz işlemleri burada gerçekleştirin.
                    }
                } else {
                    Log.e("YouTubeApiHelper", "Video listesi alınamadı");
                }
            }
        }
    }
}
