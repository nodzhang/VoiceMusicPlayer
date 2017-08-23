package com.hackathon.cmos.www.voicemusicplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackathon.cmos.www.voicemusicplayer.R;
import com.hackathon.cmos.www.voicemusicplayer.activity.LoginActivity;
import com.hackathon.cmos.www.voicemusicplayer.ui.CircleImageView;

import org.w3c.dom.Text;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zhangxi on 2017/8/16.
 */

public class MinePageFragment extends Fragment {
    private CircleImageView imageTouXiang;
    private TextView tv_userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, null);

        imageTouXiang = view.findViewById(R.id.ivPhoto);
        tv_userName = view.findViewById(R.id.tvNickname);

        setListeners();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnData = data.getStringExtra("username");
                    tv_userName.setText(returnData);
                }
                    break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setListeners() {
        imageTouXiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 1);
//                startActivity(intent);
            }
        });
    }
}
