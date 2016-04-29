package com.example.glushkov.zen;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<CheckBox> cbList = new ArrayList<>();
    ImageButton ivBtnStart;
    long DURATION = 450l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Figure2 figure2 = (Figure2) findViewById(R.id.figure2);

        ivBtnStart = (ImageButton) findViewById(R.id.ivBtnStart);
        ivBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                figure2.startRotate();
            }
        });

        CheckBox cbState_1 = (CheckBox) findViewById(R.id.cbState_1);
        CheckBox cbState_2 = (CheckBox) findViewById(R.id.cbState_2);
        CheckBox cbState_3 = (CheckBox) findViewById(R.id.cbState_3);
        CheckBox cbState_4 = (CheckBox) findViewById(R.id.cbState_4);

        cbList.add(cbState_1);
        cbList.add(cbState_2);
        cbList.add(cbState_3);
        cbList.add(cbState_4);

        CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (CheckBox cb : cbList) {
                    cb.setElevation(0);
                    cb.setChecked(false);
                }
                if (isChecked) {
                    buttonView.setChecked(true);
                    buttonView.setElevation(getResources().getDimension(R.dimen._elevation));
                    showRoundButton(DURATION);
                } else {
                    hideRoundButton(DURATION);
                }
            }
        };

        cbState_1.setOnCheckedChangeListener(checkListener);
        cbState_2.setOnCheckedChangeListener(checkListener);
        cbState_3.setOnCheckedChangeListener(checkListener);
        cbState_4.setOnCheckedChangeListener(checkListener);

        hideRoundButton(0);

    }

    private void showRoundButton(long d) {
        ivBtnStart.setEnabled(true);
        playAnimation(new ObjectAnimator[]{
                ObjectAnimator.ofFloat(ivBtnStart, View.SCALE_X, 0.0f, 1.0f),
                ObjectAnimator.ofFloat(ivBtnStart, View.SCALE_Y, 0.0f, 1.0f)
        }, d);
    }

    private void hideRoundButton(long d) {
        ivBtnStart.setEnabled(false);
        playAnimation(new ObjectAnimator[]{
                ObjectAnimator.ofFloat(ivBtnStart, View.SCALE_X, 1.0f, 0.0f),
                ObjectAnimator.ofFloat(ivBtnStart, View.SCALE_Y, 1.0f, 0.0f)
        }, d);
    }

    private void playAnimation(ObjectAnimator[] a, long d) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(a);
        set.setDuration(d);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }
}
