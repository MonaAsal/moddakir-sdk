package com.moddakir.call.view.agora;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.moddakir.call.API.ApiManager;
import com.moddakir.call.Adapters.ConnectingScreenAdapter;
import com.moddakir.call.Adapters.DotsIndicatorDecoration;
import com.moddakir.call.Model.BaseResponse;
import com.moddakir.call.Model.CallLog;
import com.moddakir.call.Model.User;
import com.moddakir.call.view.widget.ButtonCalibriBold;
import com.moddakir.call.SinchEx.AudioPlayer;
import com.moddakir.call.SinchEx.MainCallScreen;
import com.moddakir.call.view.widget.PlayGifView;
import com.moddakir.call.view.widget.TextViewCalibriBold;
import com.moddakir.call.view.widget.TextViewLateefRegOT;
import com.moddakir.call.view.widget.TextViewUniqueLight;
import com.moddakir.call.utils.CallStatus;
import com.moddakir.call.utils.LogFile;
import com.moddakir.call.utils.SharedPrefUtil;
import com.moddakir.call.utils.Utils;
import com.moddakir.call.Model.ConnectingBannersResponse;
import com.moddakir.call.Model.ConsumedPakageResponseModel;
import com.moddakir.call.Model.CreateCallResponseModel;
import com.moddakir.call.Model.LoginResponseModel;
import com.moddakir.call.Model.RandomTeacherResponseModel;
import com.moddakir.call.Model.ResponseModel;
import com.moddakir.call.R;
import com.moddakir.call.utils.AccountPreference;
import com.moddakir.call.utils.Perference;
import com.moddakir.call.view.evaluation.TeacherEvaluationActivity;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Response;
import timber.log.Timber;

public class AgoraActivity extends MainCallScreen
        implements
        View.OnClickListener {
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO};
    String channelName = "", isVideo = "false", token = "", LOG_TAG = "AgoraActivity";
    private RecyclerView viewPager;
    String gender, name, phone, email, language;
    Boolean IsConnectRTC = false, isVisible = false, isReachingTheTeacher = false, isHangupByStudent = false, isShowBandMessage = false, isJoined = false, TeacherJoin = false, handlerStopped = false, ismute = false, saveRetryCounts = false, TeacherConnected = false, isspeaker = false, isShowed = false, isEndCallByUser = false;
    CreateCallResponseModel RTCData = null;
    int maxRingDurationMillis, NotAvailable = 0, callDurationSeconds = 0, video_status = 2, reason = 0, maxCallDuration, uid = 0, UDID = 0;
    ;
    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;
    ConstraintLayout clConnecting;
    RelativeLayout errorRelative, succesRelative;
    User teacher, user;
    Disposable disposable;
    private final double publishAudioLosePercentage = 0;
    private final double publishVideoLosePercentage = 0;
    private final double subscribAudioLosePercentage = 0;
    private final double subscribVideoLosePercentage = 0;
    private AudioPlayer mAudioPlayer;
    private CircleImageView civ_teacher_image1, civ_teacher_image;
    private FrameLayout warning_frame;
    private TextViewUniqueLight mCallDuration;
    private TextViewCalibriBold mCallerName1;

    private ButtonCalibriBold btnEndCall;

    LinearLayout hang_up;
    private ImageButton btnMute, btnSpeaker, btnEndCall1;

    private View vOnCall, vCalling;

    private Timer mTimer;
    private ConsumedPakageResponseModel consumedPakageResponseModel;
    private TextViewLateefRegOT speed_status, tv_internet_status;
    private SweetAlertDialog pDialog, countDownDialog;
    private CallLog callLog;
    private CreateCallResponseModel createCallResponseModel;
    private CountDownTimer maxDurationCountDown, ringDurationCountDown, startTeacherNotAvailable;
    private UpdateCallDurationTask mDurationTask;
    private AudioManager audioManager;
    private RelativeLayout rl_decline, re1, rel2;
    private STATUS status;
    private STATE state = STATE.INIT;
    private RtcEngine agoraEngine;
    private SurfaceView remoteSurfaceView;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {

            TeacherJoin = true;
            mAudioPlayer.stopProgressTone();
           // runOnUiThread(() -> setupRemoteVideo(uid));
            civ_teacher_image1.setVisibility(View.VISIBLE);
            runOnUiThread(() ->
            {
                vOnCall.setVisibility(View.VISIBLE);
                vCalling.setVisibility(View.GONE);
                isVisible = true;
                rl_decline.setVisibility(View.VISIBLE);
                re1.setVisibility(View.VISIBLE);
                rel2.setVisibility(View.VISIBLE);
                mTimer = new Timer();
                mDurationTask = new UpdateCallDurationTask();
                mTimer.schedule(mDurationTask, 0, 1000);

            });
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            SendErrorCode(reason);
            state = STATE.ERORR;
            endCall();
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
        }

        @Override
        public void onConnectionInterrupted() {
            showCallInterruptedMessage(R.string.call_interrupted_message_teacher);
            super.onConnectionInterrupted();
        }

        @Override
        public void onConnectionLost() {
            super.onConnectionLost();
            SendMsg("ConnectionLost");
        }

        @Override
        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onRejoinChannelSuccess(channel, uid, elapsed);
            isJoined = true;
            if (TeacherJoin) {
                vOnCall.setVisibility(View.VISIBLE);
                vCalling.setVisibility(View.GONE);
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            IsConnectRTC = true;
            try {
                runOnUiThread(() -> {
                    clConnecting.setVisibility(View.GONE);
                });
            } catch (Exception e) {

            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {

            if (startTeacherNotAvailable != null) startTeacherNotAvailable.cancel();
            if (callDurationSeconds > 30) {
                TeacherEvaluationActivity.start
                        (AgoraActivity.this, teacher.getId(),
                                teacher.getFullName(),
                                callLog.getCid(),
                                teacher.getAvatarurl(),
                                AccountPreference.getUser(AgoraActivity.this)
                        );

                status = STATUS.ENDED;

            } else {
                backToHomePage();
            }
            finish();

        }

    };

    private void handleView() {
        mAudioPlayer.stopProgressTone();
       // runOnUiThread(() -> setupRemoteVideo(uid));
        civ_teacher_image1.setVisibility(View.VISIBLE);
        runOnUiThread(() ->
        {
            vOnCall.setVisibility(View.VISIBLE);
            vCalling.setVisibility(View.GONE);
            isVisible = true;
            rl_decline.setVisibility(View.VISIBLE);
            re1.setVisibility(View.VISIBLE);
            rel2.setVisibility(View.VISIBLE);
            mTimer = new Timer();
            mDurationTask = new UpdateCallDurationTask();
            mTimer.schedule(mDurationTask, 0, 1000);

        });


    }

    public void changeMute() {
        if (ismute) {
            agoraEngine.enableAudio();
            agoraEngine.adjustRecordingSignalVolume(100);
            btnMute.setBackgroundResource(R.drawable.btn_mute_oncall);
            ismute = false;
        } else {
            agoraEngine.adjustRecordingSignalVolume(0);
            btnMute.setBackgroundResource(R.drawable.unmute);
            ismute = true;
        }
    }

    public void changeSpeaker() {
        if (audioManager != null)

            if (isspeaker) {
                audioManager.setSpeakerphoneOn(false);
                isspeaker = false;
                btnSpeaker.setBackgroundResource(R.drawable.btn_speaker_oncall);
            } else {
                audioManager.setSpeakerphoneOn(true);
                isspeaker = true;
                btnSpeaker.setBackgroundResource(R.drawable.speaker);
            }
    }

    private void createCallAndGetAvadMin(String unfreezeAccount) {
        HashMap<String, String> map = new HashMap<>();
        User user = AccountPreference.getUser(AgoraActivity.this);
        map.put("callProviderType", "agora");
        if (user != null) {
            map.put("studentId", user.getId());
            map.put("updaterId", user.getId());
            map.put("studentName", user.GetCalleeName());
            map.put("studentAvatarUrl", user.getAvatarurl());
            map.put("studentCountry", user.getCountry());
            map.put("sinchAppKey", Perference.getSinchAppKey(this));
            map.put("unfreezeAccount", unfreezeAccount);
            map.put("callType", "Voice");
        }
        if (teacher != null) {
            map.put("teacherId", teacher.getId());
            map.put("teacherName", teacher.GetCalleeName());
            map.put("teacherAvatarUrl", teacher.getAvatarurl());
        }
        map.put("status", "CREATED");
        Log.e("CreateCallLog", map.toString());
        (new ApiManager(AgoraActivity.this).getUserCalls(true)).CreateCall(map)
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread()) // “listen” on UIThread
                .map(responseModel -> responseModel)
                .subscribe(new DisposableSingleObserver<Response<CreateCallResponseModel>>() {
                    @Override
                    public void onSuccess(Response<CreateCallResponseModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("CREATE_CALL_RESPONSE", new Gson().toJson(response.body()));
                            if (response.body().getStatusCode() == 417) {
                            } else if (response.body().getStatusCode() == 200) {
                                response.body().getRingingDuration();
                                setupSignaling(response.body().getCallApiKey(), response.body().getStudentSignalingToken());
                                token = response.body().getStudentSessionToken();
                                channelName = response.body().getCall().getCallSessionId();
                                RTCData = response.body();
                                startCall(response.body());
                            } else if (response.body().getStatusCode() == 404) {

                                if (response != null && response.body() != null) {
                                    HideButtons();
                                    onServiceDisconnected(true);
                                } else {
                                    playEndCallSound();
                                    finish();
                                }
                            } else if (response.body().getStatusCode() == 406) {
                                playEndCallSound();
                                finish();
                            } else {
                                onServiceDisconnected(true);
                            }
                        } else {
                            showMessage(getResources().getString(R.string.server_error));
                            onServiceDisconnected(true);
                        }
//
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e("exception", e.toString());
                        if (e instanceof java.net.ConnectException || e instanceof java.net.UnknownHostException || e instanceof SocketTimeoutException) {
                            showMessage(getResources().getString(R.string.internetConnectionError));
                        }
                        onServiceDisconnected(true);
                    }
                });


    }

    private void startCall(CreateCallResponseModel createCallResponse) {
        Timber.i("startCall: " + new Gson().toJson(createCallResponse));
        if (createCallResponse.getEnableCallLogs()) {
            LogFile.init(getApplicationContext(), createCallResponse.getCall().getCid() + ".txt");
            ArrayList<String> listOfCallIds = SharedPrefUtil.getSharedPref(getApplicationContext()).read(com.moddakir.call.Constants.CALL_IDS);
            if (listOfCallIds == null || listOfCallIds.size() <= 0) {
                listOfCallIds = new ArrayList<>();
            }
            listOfCallIds.add(createCallResponse.getCall().getCid());
            SharedPrefUtil.getSharedPref(getApplicationContext()).write(com.moddakir.call.Constants.CALL_IDS, listOfCallIds);
        }
        createCallResponseModel = createCallResponse;
        consumedPakageResponseModel = createCallResponseModel.getStudentDurations();
        maxCallDuration = createCallResponseModel.getMaxCallDuration();
        callLog = createCallResponseModel.getCall();
        Timber.v("Call info " + callLog.getCid());
        if (consumedPakageResponseModel != null && consumedPakageResponseModel.getRemainingMinitues() > 0) {
            CallUserSinch();
        } else {

            Toast.makeText(AgoraActivity.this, getResources().getString(R.string.dont_have_balance), Toast.LENGTH_LONG).show();
            onServiceDisconnected(true);
        }
    }

    private void connectToRTC() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.autoSubscribeAudio = true;
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        agoraEngine.startPreview();
        reason = agoraEngine.joinChannelWithUserAccount(token, channelName, user.getId(), options);
    }


    private String formatTimeSpan(int totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateCallDuration(int seconds) {
        runOnUiThread(() -> mCallDuration.setText(formatTimeSpan(seconds)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);
        addOverlay();
        CallStatus.userCallStatus = CallStatus.CALL_STATUS.IN_CALL;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioPlayer = new AudioPlayer(this);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);
        checkBultooth();
        callDurationSeconds = 0;
        status = STATUS.INIT;
        state = STATE.INIT;
        user = AccountPreference.getUser(AgoraActivity.this);
        InitViews();
        disposable = Observable.timer(3, TimeUnit.SECONDS).repeat().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(aLong -> {
                    double max = Utils.MaxNumber(publishAudioLosePercentage, publishVideoLosePercentage, subscribAudioLosePercentage, subscribVideoLosePercentage);
                    Log.e("LOSTPACKET", max + "");
                    if (max < 1.8 && max >= 0) {
                        //good
                        tv_internet_status.setText(com.moddakir.call.R.string.internet_speed_good);
                        tv_internet_status.setBackgroundResource(R.color.colorPrimary);
                        tv_internet_status.setVisibility(View.GONE);
                    } else if (max > 1.8 && max < 4.0) {
                        //weak
                        tv_internet_status.setText(com.moddakir.call.R.string.internet_speed_weak);
                        tv_internet_status.setBackgroundResource(R.color.colorGray);

                        tv_internet_status.setVisibility(View.VISIBLE);
                    } else {
                        //bad
                        tv_internet_status.setText(com.moddakir.call.R.string.internet_speed_bad);
                        tv_internet_status.setBackgroundResource(R.color.colorDarkGray);
                        tv_internet_status.setVisibility(View.VISIBLE);

                    }
                });


    }

    private void checkBultooth() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
                Log.d("Bultoooth", "ACTION_FOUND");
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                Log.d("Bultoooth", "ACTION_ACL_CONNECTED");

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
                Log.d("Bultoooth", "ACTION_DISCOVERY_FINISHED");

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                Log.d("Bultoooth", "ACTION_ACL_DISCONNECT_REQUESTED");

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Log.d("Bultoooth", "ACTION_ACL_DISCONNECTED");

            }
        }
    };

    @Override
    protected void onStart() {
        Bundle ex = getIntent().getExtras();
        if (ex != null) {
            language = ex.getString("language");
            Perference.setLang(this, language);
        }
        super.onStart();
    }

    private void InitViews() {

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = findViewById(R.id.tv_duration);
        mCallerName1 = findViewById(R.id.remoteUser1);
        btnEndCall = findViewById(R.id.iv_decline);
        hang_up = findViewById(R.id.hang_up);
        btnEndCall1 = findViewById(R.id.iv_decline1);
        civ_teacher_image1 = findViewById(R.id.civ_teacher_image1);
        civ_teacher_image = findViewById(R.id.civ_teacher_image);
        speed_status = findViewById(R.id.speed_status);
        tv_internet_status = findViewById(R.id.tv_internet_status);
        btnMute = findViewById(R.id.iv_mute);
        btnSpeaker = findViewById(R.id.iv_speaker);
        vCalling = findViewById(R.id.calling);
        vOnCall = findViewById(R.id.oncall);
        clConnecting = findViewById(R.id.clConnecting);
        AppCompatImageView ivClose = findViewById(R.id.ivClose);
        errorRelative = findViewById(R.id.error_relative);
        succesRelative = findViewById(R.id.success_relative);
        warning_frame = findViewById(R.id.warning_frame);
        re1 = findViewById(R.id.re1);
        rel2 = findViewById(R.id.rel2);
        rl_decline = findViewById(R.id.rl_decline);
        PlayGifView pGif = (PlayGifView) findViewById(R.id.viewGif);
        pGif.setImageResource(R.drawable.connecting);
        runOnUiThread(() -> {
            ivClose.setOnClickListener(this);
            btnEndCall.setOnClickListener(this);
            btnEndCall1.setOnClickListener(this);
            hang_up.setOnClickListener(this);
            btnMute.setOnClickListener(this);
            btnSpeaker.setOnClickListener(this);
        });

    }

    private void showingConnectingBanners(RecyclerView recyclerView) {
        (new ApiManager(AgoraActivity.this).getUserCalls(true)).getSdkBanners()
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread()) // “listen” on UIThread
                .map(responseModel -> responseModel)
                .subscribe(new DisposableSingleObserver<Response<ConnectingBannersResponse>>() {
                    @Override
                    public void onSuccess(Response<ConnectingBannersResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Timber.e(new Gson().toJson(response.body()));
                            if (response.body().getStatusCode() == 200) {
                                ConnectingScreenAdapter adapter = new ConnectingScreenAdapter(AgoraActivity.this, response.body().getData());
                                recyclerView.setAdapter(adapter);
                                recyclerView.setNestedScrollingEnabled(false);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AgoraActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                recyclerView.setHasFixedSize(true);
                                final int radius = 10;
                                final int dotsHeight = 30;
                                final int color = ContextCompat.getColor(AgoraActivity.this, R.color.colorPrimary);
                                recyclerView.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color));
                                new PagerSnapHelper().attachToRecyclerView(recyclerView);

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.tag("exception_update_s").e(e.toString());
                        if (e instanceof java.net.ConnectException || e instanceof java.net.UnknownHostException || e instanceof SocketTimeoutException) {
                        }
                    }
                });

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_decline1 || id == R.id.ivClose) {
            isEndCallByUser = true;
            state = STATE.CANCELLED;
            endCall();
            try {
                agoraEngine.leaveChannel();
                new Thread(() -> {
                    RtcEngine.destroy();
                    agoraEngine = null;
                }).start();

            } catch (Exception e) {

            }
        } else if (id == R.id.iv_decline || id == R.id.hang_up) {
            state = STATE.CANCELLED;
            isHangupByStudent = true;
            isEndCallByUser = true;
            endCall();
            try {
                agoraEngine.leaveChannel();
                new Thread(() -> {
                    RtcEngine.destroy();
                    agoraEngine = null;
                }).start();

            } catch (Exception e) {

            }
        } else if (id == R.id.iv_mute) {
            changeMute();
        } else if (id == R.id.iv_speaker) {
            changeSpeaker();
        }
    }

    private void SendErrorCode(int reason) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sessionId", RTCData.getCall().getCallSessionId());
        map.put("reason", reason);
        (new ApiManager(AgoraActivity.this).getUserCalls(true)).endReason(map)
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread()) // “listen” on UIThread
                .map(responseModel -> responseModel)
                .subscribe(new DisposableSingleObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(@NonNull BaseResponse freezeAccountResponseModel) {}

                    @Override
                    public void onError(@NonNull Throwable e) {}
                });
    }

    private void HideButtons() {
        btnEndCall.setVisibility(View.GONE);
        btnEndCall1.setVisibility(View.GONE);
        btnSpeaker.setVisibility(View.GONE);
        btnMute.setVisibility(View.GONE);
        rel2.setVisibility(View.GONE);
        re1.setVisibility(View.GONE);
        rl_decline.setVisibility(View.GONE);
        vOnCall.setVisibility(View.GONE);
        vCalling.setVisibility(View.VISIBLE);
    }

    private void playEndCallSound() {
        if (mAudioPlayer != null) {
            audioManager.setSpeakerphoneOn(true);
            mAudioPlayer.playEndCallSound();
        }
    }

    private void endCall() {
        handlerStopped = true;
        SendMsg("endCall");
        Log.e("CallDurationStudent", callDurationSeconds + "");
        HideButtons();
        if (disposable != null) disposable.dispose();
        CallStatus.userCallStatus = CallStatus.CALL_STATUS.IDLE;
        playEndCallSound();
        if (mDurationTask != null)
            mDurationTask.cancel();
        if (mTimer != null)
            mTimer.cancel();
        if (ringDurationCountDown != null) {
            ringDurationCountDown.cancel();
        }
        if (mAudioPlayer != null)
            mAudioPlayer.stopProgressTone();
        if (maxDurationCountDown != null)
            maxDurationCountDown.cancel();

        if (startTeacherNotAvailable != null) startTeacherNotAvailable.cancel();


        if (status == STATUS.ENDED)
            return;
        status = STATUS.ENDED;
        if (state == STATE.NO_ANSWER) {
            requestUpdateCall("NO_ANSWER");
            showApologizeMessage(R.string.apologize);
            return;
        }
        if (state == STATE.ERORR || state == STATE.INIT || state == STATE.DENIED && !isReachingTheTeacher) {
            showApologizeMessage(R.string.apologize);
            return;
        }
        if (state == STATE.CANCELLED) {
            if (callDurationSeconds >= 30) {
                requestUpdateCall("HUNG_UP");
                TeacherEvaluationActivity.start
                        (this, teacher.getId(), teacher.getFullName(), callLog.getCid(), teacher.getAvatarurl(), AccountPreference.getUser(AgoraActivity.this));
                finish();
            } else {
                requestUpdateCall("HUNG_UP");
                onServiceDisconnected(false);
            }
            return;
        }
        try {
            agoraEngine.leaveChannel();
            new Thread(() -> {
                RtcEngine.destroy();
                agoraEngine = null;
            }).start();

        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        CallStatus.userCallStatus = CallStatus.CALL_STATUS.IDLE;
        if (startTeacherNotAvailable != null) startTeacherNotAvailable.cancel();

        if (pDialog != null) {
            pDialog.dismiss();
        }
        if (mDurationTask != null)
            mDurationTask.cancel();

        if (maxDurationCountDown != null) maxDurationCountDown.cancel();
        if (ringDurationCountDown != null) ringDurationCountDown.cancel();
        if (mTimer != null)
            mTimer.cancel();
        if (countDownDialog != null) {
            countDownDialog.dismiss();
        }

        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();

        if (mAudioPlayer != null) {
            mAudioPlayer.stopRingtone();
            mAudioPlayer.stopProgressTone();

        }
        if ((Vibrator) AgoraActivity.this.getSystemService(VIBRATOR_SERVICE) != null) {
            ((Vibrator) AgoraActivity.this.getSystemService(VIBRATOR_SERVICE)).cancel();
        }
        try {
            mRtmClient.logout(new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                }
            });
            agoraEngine.leaveChannel();

            new Thread(() -> {
                RtcEngine.destroy();
                agoraEngine = null;
            }).start();
        } catch (Exception e) {

        }

        super.onDestroy();
    }

    @Override
    protected void StatusChanged(int integer) {
        if (integer == 1) {
            speed_status.setText(com.moddakir.call.R.string.internet_speed_low);
            tv_internet_status.setText(com.moddakir.call.R.string.internet_speed_low);
            tv_internet_status.setBackgroundResource(R.color.colorGray);
            speed_status.setBackgroundResource(R.color.colorGray);
        } else if (integer == 2) {
            speed_status.setText(com.moddakir.call.R.string.internet_speed_good);
            tv_internet_status.setText(com.moddakir.call.R.string.internet_speed_good);
            tv_internet_status.setBackgroundResource(R.color.colorPrimary);
            speed_status.setBackgroundResource(R.color.colorPrimary);
        } else if (integer == 0) {
            speed_status.setText(com.moddakir.call.R.string.no_internet_speed);
            tv_internet_status.setText(com.moddakir.call.R.string.no_internet_speed);
            tv_internet_status.setBackgroundResource(R.color.colorDarkGray);
            speed_status.setBackgroundResource(R.color.colorDarkGray);
        }
    }

    @Override
    public void OpenBackground() {
        Intent openMainActivity = new Intent(getApplicationContext(), AgoraActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMainActivity, 0);
    }

    protected void onServiceDisconnected(boolean playEndCallSound) {
        if (playEndCallSound)
            playEndCallSound();
        if (createCallResponseModel != null)
            Observable.timer(createCallResponseModel.getDelayTimeAfterEndingCall(), TimeUnit.SECONDS).subscribe(aLong -> {
                backToHomePage();
                finish();

            });
        else {
            Observable.timer(4, TimeUnit.SECONDS).subscribe(aLong -> {
                backToHomePage();
                finish();

            });
        }
    }

    private void RingDurationCountDown() {
        maxRingDurationMillis = createCallResponseModel.getRingingDuration() * 1000;
        ringDurationCountDown = new CountDownTimer(maxRingDurationMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.e("RingDurationCountDown", String.valueOf(millisUntilFinished / 1000));
            }
            public void onFinish() {
                state = STATE.NO_ANSWER;
                endCall();
            }
        }.start();
    }

    private void CallUserSinch() {
        if (status == STATUS.ENDED && isEndCallByUser) {
            requestUpdateCall("CANCELED");
            return;
        } else if (status == STATUS.ENDED) {
            return;
        }
        if (teacher != null) {
            mCallerName1.setText(teacher.GetCalleeName());
            Utils.loadAvatar(this, teacher.getAvatarurl(), civ_teacher_image);
            Utils.loadAvatar(this, teacher.getAvatarurl(), civ_teacher_image1);
            User user = AccountPreference.getUser(AgoraActivity.this);
            if (user != null) {
                Map<String, String> headers = new HashMap<>();
                if (user.getAvatarurl() != null && !user.getAvatarurl().trim().isEmpty())
                    headers.put("avatarUrl", user.getAvatarurl());
                headers.put("displayName", user.GetCalleeName());
                headers.put("isVideo", isVideo);
                if (callLog != null) {
                    headers.put("callId", callLog.getCid());
                }
            }

        }
        LogFile.d(LOG_TAG, "onConnectedSession");
        // mCallState.setText(getString(R.string.TheTeacherIsBeingReached));

        startTeacherNotAvailable = new CountDownTimer(30 * 1000L, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
                Timber.i("onFinish CountDownTimer: " + millisUntilFinished);
            }

            public void onFinish() {
                mAudioPlayer.stopProgressTone();
                TeacherConnected = false;
                status = STATUS.INIT;
                getRandomTeacher();
            }

        }.start();
    }

    private void backToHomePage() {
        CallStatus.userCallStatus = CallStatus.CALL_STATUS.IDLE;
        finish();
    }

    private void showApologizeMessage(int message) {
        if (!((Activity) AgoraActivity.this).isFinishing())
            return;
        SweetAlertDialog pDialog = new SweetAlertDialog(AgoraActivity.this, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(getResources().getString(message));
        pDialog.setCancelable(false);
        pDialog.setConfirmText(getResources().getString(R.string.ok));
        pDialog.setConfirmClickListener(sweetAlertDialog -> {
            pDialog.dismissWithAnimation();
            mAudioPlayer.stopProgressTone();
            backToHomePage();
            finish();
        });
        pDialog.show();
        Utils.changeSweetAlertDialogStyle(pDialog, this);
    }

    private void requestUpdateCall(String status) {
        Timber.tag("requestUpdateCall").e("Student");
        HashMap<String, Object> map = new HashMap<>();
        if (callLog != null) map.put("callId", callLog.getCid());
        map.put("status", status);
        if (isEndCallByUser) {
            map.put("isHangupByTeacher", false);
        }
        if (isHangupByStudent) {
            map.put("isHangupByStudent", true);
        }
        map.put("lang", Perference.getLang(this));
        (new ApiManager(AgoraActivity.this).getUserCalls(true)).UpdateCall(map)
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread()) // “listen” on UIThread
                .map(responseModel -> responseModel)
                .subscribe(new DisposableSingleObserver<Response<ResponseModel>>() {
                    @Override
                    public void onSuccess(Response<ResponseModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    private enum STATUS {INIT, RING, INCALL, ENDED}
    private enum STATE {INIT, ENDED, ERORR, CANCELLED, NO_ANSWER, DENIED, NOTAVA}

    private class UpdateCallDurationTask extends TimerTask {
        @Override
        public void run() {
            isReachingTheTeacher = true;
            callDurationSeconds += 1;
            AgoraActivity.this.runOnUiThread(() ->
                    updateCallDuration(callDurationSeconds));
        }
    }


    private void showCallInterruptedMessage(int message) {
        SweetAlertDialog pDialog = new SweetAlertDialog(AgoraActivity.this, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText(getResources().getString(message));
        pDialog.setCancelable(false);
        pDialog.setConfirmText(getResources().getString(R.string.ok));
        pDialog.setConfirmClickListener(sweetAlertDialog -> {
            pDialog.dismiss();
        });
        pDialog.show();
        Utils.changeSweetAlertDialogStyle(pDialog, this);

    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }


    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.v_remote_video);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        remoteSurfaceView.setVisibility(View.VISIBLE);
        remoteSurfaceView.setVisibility(View.GONE);

    }

    private void setupVideoSDKEngine(String callApiKey) {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = callApiKey;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            agoraEngine.setAINSMode(true, 0);

        } catch (Exception e) {
        }
    }

    private void setupSignaling(String AppID, String token) {

        try {
            mRtmClient = RtmClient.createInstance(getBaseContext(), AppID, new RtmClientListener() {
                @Override
                public void onConnectionStateChanged(int state, int reason) {
                }
                @Override
                public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {
                }

                @Override
                public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {
                }

                @Override
                public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
                }

                @Override
                public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
                }

                @Override
                public void onTokenExpired() {
                }

                @Override
                public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
                }

                @Override
                public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                    String text = "Message received from " + peerId + " Message: " + rtmMessage.getText() + "\n";
                    //showMessage(text);
                }
            });
            mRtmClient.login(token, user.getId(), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    JoinSignalingChannel();
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                     CharSequence text = "User: " + user.getId() + " failed to log in to Signaling!" + errorInfo.toString();
                }
            });


        } catch (Exception e) {
            String s = e.getMessage();
        }

    }


    public void JoinSignalingChannel() {
        RtmChannelListener mRtmChannelListener = new RtmChannelListener() {
            @Override
            public void onAttributesUpdated(List<RtmChannelAttribute> list) {
            }

            @Override
            public void onMemberCountUpdated(int i) {
                if (i == 2 && TeacherJoin) {
                    if (vCalling.getVisibility() == View.VISIBLE) {
                        handleView();
                    }

                }
            }

            @Override
            public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {
            }

            @Override
            public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {
            }

            @SuppressLint("CheckResult")
            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {

                String text = message.getText();
                if (text.equals("error")) {
                    mAudioPlayer.playError();
                    runOnUiThread(() -> {
                        warning_frame.setBackgroundColor(getResources().getColor(R.color.black40opacity));
                        errorRelative.setVisibility(View.VISIBLE);
                    });

                    Observable.timer(3, TimeUnit.SECONDS).subscribe(aLong -> {
                        runOnUiThread(() -> {
                            warning_frame.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            errorRelative.setVisibility(View.GONE);
                            mAudioPlayer.stopProgressTone();
                        });
                    });

                } else if (text.equals("succeed")) {
                    runOnUiThread(() -> {

                        warning_frame.setBackgroundColor(getResources().getColor(R.color.transparent));

                        succesRelative.setVisibility(View.VISIBLE);
                        // shineButton.callOnClick();
                    });

                    Observable.timer(3, TimeUnit.SECONDS).subscribe(aLong -> {
                        runOnUiThread(() -> {

                            warning_frame.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            succesRelative.setVisibility(View.GONE);
                        });
                    });
                } else if (text.equals("reachingTheTeacher")) {
                    mAudioPlayer.playProgressTone();
                    TeacherConnected = true;
                    status = STATUS.RING;
                    RingDurationCountDown();
                } else if (text.equals("openShareScreen")) {

                } else if (text.equals("ConnectRTC")) {
                    setupVideoSDKEngine(RTCData.getCallApiKey());
                    connectToRTC();
                    if (startTeacherNotAvailable != null) {
                        startTeacherNotAvailable.cancel();
                    }


                } else if (text.equals("CallCancelled")) {
                    getRandomTeacher();
                } else if (text.equals("ConnectionLost")) {
                    tv_internet_status.setVisibility(View.VISIBLE);
                    tv_internet_status.setText(R.string.trying_reach_teacher);
                    tv_internet_status.setBackgroundResource(R.color.colorBusy);
                }

            }


            @Override
            public void onMemberJoined(RtmChannelMember member) {
            }

            @Override
            public void onMemberLeft(RtmChannelMember member) {
                Log.d("MemberLeft ", " onMemberLeft" + "");
                if (!IsConnectRTC) {
                    if (startTeacherNotAvailable != null) startTeacherNotAvailable.cancel();
                    if (callDurationSeconds > 30) {
                        TeacherEvaluationActivity.start
                                (AgoraActivity.this, teacher.getId(),
                                        teacher.getFullName(),
                                        callLog.getCid(),
                                        teacher.getAvatarurl(),
                                        AccountPreference.getUser(AgoraActivity.this)

                                );

                        status = STATUS.ENDED;
                        finish();
                    } else {
                        if (callDurationSeconds != 0) {
                            backToHomePage();
                            finish();
                        }

                    }

                } else {
                    tv_internet_status.setVisibility(View.VISIBLE);
                    tv_internet_status.setText(R.string.trying_reach_teacher);
                    tv_internet_status.setBackgroundResource(R.color.colorBusy);
                }

            }
        };

        try {
            mRtmChannel = mRtmClient.createChannel(channelName, mRtmChannelListener);
        } catch (RuntimeException e) {
        }
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                HashMap<String, Object> map = new HashMap<>();
                if (RTCData != null) map.put("sessionId", RTCData.getCall().getCallSessionId());
                (new ApiManager(AgoraActivity.this).getUserCalls(true)).JoinAgoraSignaling(map)
                        .subscribeOn(Schedulers.io()) // “work” on io thread
                        .observeOn(AndroidSchedulers.mainThread()) // “listen” on UIThread
                        .map(responseModel -> responseModel)
                        .subscribe(new DisposableSingleObserver<Response<BaseResponse>>() {
                            @Override
                            public void onSuccess(Response<BaseResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Timber.e(new Gson().toJson(response.body()));
                                    if (response.body().getStatusCode() == 200) {
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.tag("exception_update_s").e(e.toString());
                                if (e instanceof java.net.ConnectException || e instanceof java.net.UnknownHostException || e instanceof SocketTimeoutException) {
                                }
                            }
                        });

            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
            }
        });

    }

    private void SendMsg(String msg) {
        try {
            final RtmMessage message = mRtmClient.createMessage();
            message.setText(msg);
            mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                }
            });
        } catch (Exception e) {

        }


    }

    private void loginSDK(String gender, String name, String phone, String email) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("gender", gender);
        map.put("fullName", name);
        // map.put("phone", phone);
        map.put("email", email);
        (new ApiManager(AgoraActivity.this).getUserCalls(false)).loginToSdk(map)
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread()) // “listen” on UIThread
                .map(responseModel -> responseModel)
                .subscribe(new DisposableSingleObserver<Response<LoginResponseModel>>() {
                    @Override
                    public void onSuccess(Response<LoginResponseModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Timber.e(new Gson().toJson(response.body()));
                            if (response.body().getStatusCode() == 200) {
                                User currentUser = response.body().getStudent();
                                currentUser.setAccessToken(response.body().getAccessToken());
                                AccountPreference.registerData(currentUser, AgoraActivity.this);
                                user = currentUser;
                                getRandomTeacher();
                                viewPager = findViewById(R.id.connecting_screen);
                                viewPager.setLayoutManager(new LinearLayoutManager(AgoraActivity.this, LinearLayoutManager.HORIZONTAL, true));
                                showingConnectingBanners(viewPager);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });


    }

    private void getRandomTeacher() {
        (new ApiManager(AgoraActivity.this).getUserCalls(true)).getRandomTeacher()
                .subscribeOn(Schedulers.io()) // “work” on io thread
                .observeOn(AndroidSchedulers.mainThread()) // “listen” on UIThread
                .map(responseModel -> responseModel)
                .subscribe(new DisposableSingleObserver<Response<RandomTeacherResponseModel>>() {
                    @Override
                    public void onSuccess(Response<RandomTeacherResponseModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Timber.e(new Gson().toJson(response.body()));
                            if (response.body().getStatusCode() == 200) {
                                User teacherModel = new User();
                                if (response.body().getTeacher() != null) {
                                    teacher = teacherModel;
                                    teacher = response.body().getTeacher();
                                    if (teacher != null) {
                                        showMessage(teacher.getFullName());
                                        mCallerName1.setText(teacher.GetCalleeName());
                                        Utils.loadAvatar(AgoraActivity.this, teacher.getAvatarurl(), civ_teacher_image);
                                        Utils.loadAvatar(AgoraActivity.this, teacher.getAvatarurl(), civ_teacher_image1);
                                        createCallAndGetAvadMin("false");
                                    }
                                } else {
                                    showMessage("no teacher now");
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            if (!handlerStopped) {
                                                if (!saveRetryCounts) {
                                                    int retryDuration = response.body().getRetryDuration();
                                                    NotAvailable = retryDuration / 10;
                                                    saveRetryCounts = true;
                                                }
                                                if (NotAvailable > 0) {
                                                    getRandomTeacher();
                                                    NotAvailable--;
                                                } else {
                                                    showMessage(getResources().getString(R.string.no_teachers));
                                                    finish();
                                                }
                                            }
                                        }
                                    }, 10000);
                                }

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public static void makeCall(Context context, String gender, String name, String phone, String email, String language) {
        Boolean validEmail=false;
        gender = gender.toLowerCase();
        language = language.toLowerCase();
        if (email != null)
        {
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                validEmail=true;
            }
        }
        if (!gender.equals("male") && !gender.equals("female")) {
            Toast.makeText(context, context.getString(R.string.valid_gender), Toast.LENGTH_LONG).show();
        } else if (name.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.name_req), Toast.LENGTH_LONG).show();
        } else if (email.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.email_req), Toast.LENGTH_LONG).show();
        }
        else if (!validEmail) {
            Toast.makeText(context, context.getString(R.string.valid_email), Toast.LENGTH_LONG).show();
        }
        else if (!language.equals("ar") && !language.equals("en") && !language.equals("fr") && !language.equals("in") && !language.equals("ur")) {
            Toast.makeText(context, context.getString(R.string.valid_language), Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(context, AgoraActivity.class);
            intent.putExtra("language", language);
            intent.putExtra("email", email);
            intent.putExtra("gender", gender);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            context.startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (EasyPermissions.hasPermissions(AgoraActivity.this, REQUESTED_PERMISSIONS)) {
            StartSdk();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void addOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                askDrawPermission();
            } else {
                StartSdk();
            }
        } else {
            StartSdk();
        }
    }

    @TargetApi(23)
    public void askDrawPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }

    private void StartSdk() {
        Bundle ex = getIntent().getExtras();
        if (ex != null) {
            gender = ex.getString("gender");
            name = ex.getString("name");
            phone = ex.getString("phone");
            email = ex.getString("email");

            if (EasyPermissions.hasPermissions(AgoraActivity.this, REQUESTED_PERMISSIONS)) {
                loginSDK(gender, name, phone, email);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                addOverlay();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                addOverlay();
            }
        }
    }
}
