package com.app.yourrecipeapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.yourrecipeapp.R;
import com.app.yourrecipeapp.config.AppConfig;
import com.app.yourrecipeapp.databases.prefs.SharedPref;
import com.app.yourrecipeapp.utils.TrackSelectionDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ActivityVideoPlayer extends AppCompatActivity {

    private static final String TAG = "ActivityStreamPlayer";
    String videoUrl;
    private StyledPlayerView styledPlayerView;
    private ExoPlayer exoPlayer;
    private DefaultDataSource.Factory dataSourceFactory;
    private ProgressBar progressBar;
    boolean fullscreen = false;
    private ImageView fullscreenButton;
    RelativeLayout parent_view;
    SharedPref sharedPref;
    Boolean contentLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_black));
            this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_black));
            this.getWindow().getDecorView().setSystemUiVisibility(0);
        }

        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("video_url");

        sharedPref = new SharedPref(this);

        parent_view = findViewById(R.id.parent_view);
        progressBar = findViewById(R.id.progressBar);

        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true).setUserAgent(getUserAgent());
        dataSourceFactory = new DefaultDataSource.Factory(getApplicationContext(), httpDataSourceFactory);

        LoadControl loadControl = new DefaultLoadControl();

        AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this, trackSelectionFactory);

        exoPlayer = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build();

        styledPlayerView = findViewById(R.id.exoPlayerView);
        styledPlayerView.setPlayer(exoPlayer);
        styledPlayerView.setUseController(true);
        styledPlayerView.requestFocus();

        playerOrientation();

        Uri uri = Uri.parse(videoUrl);

        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onCues(@NonNull List<Cue> cues) {

            }

            @Override
            public void onTimelineChanged(@NonNull Timeline timeline, int reason) {

            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == PlaybackStateCompat.STATE_PLAYING) {
                    progressBar.setVisibility(View.GONE);
                    contentLoaded = true;
                }

                switch (state) {
                    case Player.STATE_READY:
                        progressBar.setVisibility(View.GONE);
                        exoPlayer.setPlayWhenReady(true);
                        break;
                    case Player.STATE_ENDED:
                        exoPlayer.seekTo(0);
                        break;
                    case Player.STATE_BUFFERING:
                        progressBar.setVisibility(View.VISIBLE);
                        exoPlayer.seekTo(0);
                        break;
                    case Player.STATE_IDLE:
                        break;
                }

            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                exoPlayer.stop();
                errorDialog();
                Log.d(TAG, "onPlayerError " + error);
            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                Log.d(TAG, "onPlayerErrorChanged " + error);
            }

        });

        styledPlayerView.findViewById(R.id.exo_track_selection_view).setOnClickListener(view -> {
            if (contentLoaded) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
                DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(
                        trackSelector, dismissedDialog -> {
                        });
                trackSelectionDialog.show(getSupportFragmentManager(), null);
            } else {
                Toast.makeText(getApplicationContext(), R.string.please_wait, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void playerOrientation() {
        fullscreenButton = styledPlayerView.findViewById(R.id.exo_fullscreen_icon);
        fullscreenButton.setOnClickListener(view -> {
            if (fullscreen) {
                setPortrait();
            } else {
                setLandscape();
            }
        });

        if (AppConfig.FORCE_VIDEO_PLAYER_TO_LANDSCAPE) {
            setLandscape();
        }

    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setPortrait() {
        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ActivityVideoPlayer.this, R.drawable.ic_exo_fullscreen_open));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) styledPlayerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        styledPlayerView.setLayoutParams(params);
        fullscreen = false;
    }

    private void setLandscape() {
        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ActivityVideoPlayer.this, R.drawable.ic_exo_fullscreen_close));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) styledPlayerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        styledPlayerView.setLayoutParams(params);
        fullscreen = true;
    }

    @SuppressLint("SwitchIntDef")
    private MediaSource buildMediaSource(Uri uri) {
        MediaItem mMediaItem = MediaItem.fromUri(Uri.parse(String.valueOf(uri)));
        int type = TextUtils.isEmpty(null) ? Util.inferContentType(uri) : Util.inferContentType("." + null);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mMediaItem);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .setAllowChunklessPreparation(true)
                        .createMediaSource(mMediaItem);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory, new DefaultExtractorsFactory())
                        .createMediaSource(mMediaItem);
            case C.TYPE_RTSP:
                return new RtspMediaSource.Factory()
                        .createMediaSource(MediaItem.fromUri(uri));
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private String getUserAgent() {

        StringBuilder result = new StringBuilder(64);
        result.append("Dalvik/");
        result.append(System.getProperty("java.vm.version"));
        result.append(" (Linux; U; Android ");

        String version = Build.VERSION.RELEASE;
        result.append(version.length() > 0 ? version : "1.0");

        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }

        String id = Build.ID;

        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }

        result.append(")");
        return result.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.getPlaybackState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.getPlaybackState();
    }

    public void errorDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.whops))
                .setCancelable(false)
                .setMessage(getString(R.string.msg_failed_stream))
                .setPositiveButton(getString(R.string.dialog_retry), (dialog, which) -> retryLoad())
                .setNegativeButton(getString(R.string.dialog_no), (dialogInterface, i) -> finish())
                .show();
    }

    public void retryLoad() {
        Uri uri = Uri.parse(videoUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

}
