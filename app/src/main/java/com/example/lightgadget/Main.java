package com.example.lightgadget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.github.anastr.speedviewlib.Gauge;
import com.github.anastr.speedviewlib.ImageSpeedometer;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.Section;
import com.github.anastr.speedviewlib.components.Style;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.BoomPiece;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.triggertrap.seekarc.SeekArc;
import com.xw.repo.BubbleSeekBar;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import co.aenterhy.toggleswitch.ToggleSwitchButton;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;

public class Main extends AppCompatActivity {
    TextView speedTv;
    RelativeLayout layout;
    Button settings, add2, add3;
    BubbleSeekBar numBar;
    SpeedView speedometer;
    SeekArc speedArc, brightArc;
    WheelView wheelView;
    ImageView color1, color2, color3;
    HSLColorPicker colorPicker;
    JellyToggleButton toggle;
    BoomMenuButton stripesMenu;
    ToggleSwitchButton joystick;

    Animation fadeIn, fadeOut, rotate;

    public MyBTclass bt = new MyBTclass();

    // Variables related to the color picker.
    int colorId;
    String[] groupIds = {"color_picker", "color_sample1", "add2", "color_sample2", "add3", "color_sample3"}; //, "joystick"};
    String[][] prohibitedIds = {{"color_sample2", "add3", "color_sample3"}, {"add2", "color_sample3"}, {"add2", "add3"}};
    
    // Variables to adjust the background according the bright.
    int R_day_start = 128;
    int G_day_start = 222;
    int B_day_start = 234;
    int R_day_end = 251;
    int G_day_end = 251;
    int B_day_end = 132;
    int R_night_start = 3;
    int G_night_start = 8;
    int B_night_start = 30;
    int R_night_end = 42;
    int G_night_end = 53;
    int B_night_end = 94;

    // Variables related to the data frame.
    int palette = 0;
    int bright = 50;
    int num = 0;
    int maxNum = 10;
    int rotation = 0;
    int flagNum = 1;
    float speed = 0;
    float maxSpeed = 100;
    Boolean[] targets;

    // Wheel view icons.
    final int[] items = {R.drawable.rand, R.drawable.rainbow, R.drawable.fire, R.drawable.water_wave, R.drawable.leaves, R.drawable.flamingo, R.drawable.police, R.drawable.color_ball, R.drawable.palm, R.drawable.sol};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        layout = findViewById(R.id.main_layout);

        // Compute the equivalent pixels for one dp.
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int dps = (int) (1 * scale + 0.5f);

        // Define the animations.
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.add_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.add_out);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_360);

        // Settings button.
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.startAnimation(rotate);
                Intent intent = new Intent(Main.this, Dashboard.class);
                startActivity(intent);
                overridePendingTransition(R.anim.down_animation, R.anim.null_animation);
                finish();
            }
        });

        // Request required permissions.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

//        // Check the state of the stripes (if BT is already connected) and create the stripes boom menu.
//        stripesMenu = findViewById(R.id.stripes_menu);
//        if (bt.getBTSocket() != null) {
////            bt.write("1", getBaseContext());
////            String answer = bt.read(getBaseContext());
////            parseState(answer);
//
//            stripesMenu.setVisibility(View.VISIBLE);
//            String[] stripesName = bt.getNames();
//            stripesMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_1);
//            stripesMenu.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_1);
//            int i = 0;
//            targets = new Boolean[stripesName.length];
//            for (String s : stripesName) {
//                stripesMenu.addBuilder(new TextInsideCircleButton.Builder()
//                        .normalText(s)
//                );
//            }
//        }

        // Check the state of the stripes (if BT is already connected) and create the stripes boom menu.
        HorizontalScrollView sv = findViewById(R.id.scroll_bar);
        // Create a LinearLayout element
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(10*dps,0,10*dps,0);

//        if (bt.getBTSocket() != null) {
//            bt.write("1", getBaseContext());
//            String answer = bt.read(getBaseContext());
//            parseState(answer);
//        }

        // IT GOES INSIDE THE BET SOCKET CHECKING!

//            String[] stripesName = bt.getNames();
        String[] stripesName = {"one","onetwo","one","onetwo","one","onetwo"};
        int cnt = -1;
        targets = new Boolean[stripesName.length];
        for (String s : stripesName) {
            LottieAnimationView lav = new LottieAnimationView(this);
//            lav.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lav.setLayoutParams(new RelativeLayout.LayoutParams(50*dps, ViewGroup.LayoutParams.WRAP_CONTENT));
            lav.setPadding(5*dps,5*dps,5*dps,5*dps);
            lav.setAnimation(R.raw.lottie_light_bulb);
            lav.setId(++cnt);
            lav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currId = v.getId();
                    LottieAnimationView curr_lav = findViewById(currId);
                    targets[currId] = !targets[currId];
                    if (targets[currId]) {
                        curr_lav.playAnimation();
                    }
                    else {
                        curr_lav.pauseAnimation();
                    }
                }
            });
            targets[cnt] = false;
            linearLayout.addView(lav);

//            Button button = new Button(this);
//            button.setText(Integer.toString(cnt++));
//            button.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            linearLayout.addView(button);

//            //set the properties for button
//            Button btnTag = new Button(this);
//            btnTag.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            btnTag.setText(s);
//            btnTag.setBackground(getDrawable(R.drawable.stripe));
//            btnTag.setId(i++);
//            sv.addView(btnTag);
        }

        // Add the LinearLayout element to the ScrollView
        sv.addView(linearLayout);
        linearLayout.setHorizontalScrollBarEnabled(true);

        // Color picker arcs.
        colorPicker = findViewById(R.id.color_picker);
        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                switch (colorId) {
                    case 1:
                        color1.setColorFilter(color);
                        break;
                    case 2:
                        color2.setColorFilter(color);
                        break;
                    case 3:
                        color3.setColorFilter(color);
                        break;
                }
            }
        });

        // Initialize the color picker setup.
        colorId = 1;
        color1 = findViewById(R.id.color_sample1);
        colorPicker.setColor(color1.getSolidColor());
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorId = 1;
                colorPicker.setColor(color1.getSolidColor());
            }
        });

        color2 = findViewById(R.id.color_sample2);
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorId = 2;
                colorPicker.setColor(color2.getSolidColor());
            }
        });
        color2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (add3.getVisibility() == View.VISIBLE) {
                    flagNum = 1;
                    add3.startAnimation(fadeOut);
                    add3.setVisibility(View.INVISIBLE);
                    color2.startAnimation(fadeOut);
                    color2.setVisibility(View.INVISIBLE);
                    add2.startAnimation(fadeIn);
                    add2.setVisibility(View.VISIBLE);

                    if (colorId == 2) {
                        colorId = 1;
                        colorPicker.setColor(color1.getSolidColor());
                    }
                }
                return false;
            }
        });

        color3 = findViewById(R.id.color_sample3);
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorId = 3;
                colorPicker.setColor(color3.getSolidColor());
            }
        });
        color3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                flagNum = 2;
                color3.startAnimation(fadeOut);
                color3.setVisibility(View.INVISIBLE);
                add3.startAnimation(fadeIn);
                add3.setVisibility(View.VISIBLE);

                if (colorId == 3) {
                    colorId = 2;
                    colorPicker.setColor(color2.getSolidColor());
                }
                return false;
            }
        });

        add2 = findViewById(R.id.add2);
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagNum = 2;
                colorId = 2;
                add2.startAnimation(fadeOut);
                add2.setVisibility(View.INVISIBLE);
                add3.startAnimation(fadeIn);
                add3.setVisibility(View.VISIBLE);
                color2.startAnimation(fadeIn);
                color2.setVisibility(View.VISIBLE);
            }
        });

        add3 = findViewById(R.id.add3);
        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagNum = 3;
                colorId = 3;
                add3.startAnimation(fadeOut);
                add3.setVisibility(View.INVISIBLE);
                color3.startAnimation(fadeIn);
                color3.setVisibility(View.VISIBLE);
            }
        });

        // Stripes num seekbar.
        numBar = findViewById(R.id.ledsNum);
        numBar.getConfigBuilder()
                .min(0)
                .max(maxNum)
                .progress(1)
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        numBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // Circular progress bar for bright.
        brightArc = findViewById(R.id.bright);
        int max_brightArc_val = brightArc.getSweepAngle();
        brightArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                bt.write("Hola", getBaseContext());
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[] {Color.rgb(R_night_end + (R_day_end - R_night_end)*i/max_brightArc_val,G_night_end + (G_day_end - G_night_end)*i/max_brightArc_val,B_night_end + (B_day_end - B_night_end)*i/max_brightArc_val),
                                Color.rgb(R_night_start + (R_day_start - R_night_start)*i/max_brightArc_val,G_night_start + (G_day_start - G_night_start)*i/max_brightArc_val,B_night_start + (B_day_start - B_night_start)*i/max_brightArc_val)});
                gd.setCornerRadius(0f);
                layout.setBackgroundDrawable(gd);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });

        // Default-customizable modes switch.
        toggle = findViewById(R.id.toggle);

        // Set the layout according the initial default configuration (left).
        setGroupVisibility(groupIds, View.INVISIBLE);
        toggle.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state == State.RIGHT_TO_LEFT) { // Default
                    setGroupAnimation(groupIds, fadeOut);
                    setGroupVisibility(groupIds, View.INVISIBLE);
                    wheelView.startAnimation(fadeIn);
                    wheelView.setVisibility(View.VISIBLE);
                }
                else if (state == State.LEFT_TO_RIGHT) { // Custom
                    wheelView.startAnimation(fadeOut);
                    wheelView.setVisibility(View.INVISIBLE);
                    setGroupAnimation(groupIds, fadeIn);
                    setGroupVisibility(groupIds, View.VISIBLE);
                }
            }
        });

        // Wheel view menu.
        wheelView = findViewById(R.id.wheelview);
        int itemsNum = items.length;
        wheelView.setWheelItemCount(itemsNum);

        Drawable[] drawables = new Drawable[itemsNum];
        for (int i=0; i < itemsNum; i++) {
            drawables[i] = getResources().getDrawable(items[i]);
        }

        // Populate the adapter, that knows how to draw each item (as you would do with a ListAdapter).
        wheelView.setAdapter(new WheelAdapter() {
            @Override
            public Drawable getDrawable(int position) {
                return drawables[position];
            }

            @Override
            public int getCount() {
                return itemsNum;
            }
        });

        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                // The position in the adapter and whether it is closest to the selection angle.
                Toast.makeText(getApplicationContext(), "Item position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        // Circular progress bar for speed.
        speedArc = findViewById(R.id.seekArc);
        speedArc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                speed = speedArc.getProgress();
                speedometer.setSpeedAt(speed);
                return false;
            }
        });

        // Speedometer view.
        speedometer = findViewById(R.id.speed_view);
        speedometer.makeSections(5, Color.CYAN, Style.BUTT);
        List<Section> sections = speedometer.getSections();
        sections.get(0).setColor(Color.GREEN);
        sections.get(1).setColor(Color.BLUE);
        sections.get(2).setColor(Color.MAGENTA);
        speedometer.setSpeedAt(0);
        speedometer.setTextColor(sections.get(0).getColor());
        speedometer.setOnSectionChangeListener(new Function2<Section, Section, Unit>() {
            @Override
            public Unit invoke(Section section, Section section2) {
                speedTv.setTextColor(section2.getColor());
                return null;
            }
        });

        speedometer.setOnSpeedChangeListener(new Function3<Gauge, Boolean, Boolean, Unit>() {
            @Override
            public Unit invoke(Gauge gauge, Boolean aBoolean, Boolean aBoolean2) {
                speedTv.setText(Float.toString(speed)+" Hz");
                return null;
            }
        });

        // Text view.
        speedTv = findViewById(R.id.speed_val);

//        // Define the switch between custom colors and default palettes.
//        joystick = findViewById(R.id.joystick);
//        joystick.setOnTriggerListener(new ToggleSwitchButton.OnTriggerListener() {
//            @Override
//            public void toggledUp() {
//
//            }
//
//            @Override
//            public void toggledDown() {
//
//            }
//        });
    }

    public static void RgbToHsl (int red, int green, int blue, float hsl[]) {
        float r = (float) red / 255;
        float g = (float) green / 255;
        float b = (float) blue / 255;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float f;/*from  w  ww. j ava2s  . co  m*/
        if (max == min)
            f = 0;
        else if (max == r && g >= b)
            f = (60 * (g - b)) / (max - min);
        else if (max == r && g < b)
            f = 360 + (60 * (g - b)) / (max - min);
        else if (max == g)
            f = 120 + (60 * (b - r)) / (max - min);
        else if (max == b)
            f = 240F + (60F * (r - g)) / (max - min);
        else
            f = 0;
        float f1 = (max + min) / 2;
        float f2;
        if (f1 != 0 && max != min) {
            if (0 < f1 && f1 <= 0.5) {
                f2 = (max - min) / (max + min);
            } else if (f1 == 0.5) {
                f2 = (max - min) / (2.0F - (max + min));
            } else {
                f2 = 0;
            }
        } else {
            f2 = 0.0F;
        }
        hsl[0] = f;
        hsl[1] = f2;
        hsl[2] = f1;
    }

    public static float[] ColorToHsl(int color) {
        float[] hsl = new float[0];
        RgbToHsl(0xff & color >>> 16, 0xff & color >>> 8, color & 0xff, hsl);
        return hsl;
    }

    private void setGroupAnimation(String[] group, Animation animation) {
        for (String id: group) {
            int Id = getResources().getIdentifier(id, "id", this.getPackageName());
            if (!(group == groupIds & Arrays.asList(prohibitedIds[flagNum - 1]).contains(id))) {
                findViewById(Id).startAnimation(animation);
            }
        }
    }

    private void setGroupVisibility(String[] group, int visibility) {
        for (String id: group) {
            int Id = getResources().getIdentifier(id, "id", this.getPackageName());
            if (!(group == groupIds & Arrays.asList(prohibitedIds[flagNum - 1]).contains(id))) {
                findViewById(Id).setVisibility(visibility);
            }
        }
    }

    private void parseState(String state) {
        String[] stateSplit = state.split(";");
        palette = 0;
        bright = 50;
        num = 0;
        maxNum = 10;
        rotation = 0;
        flagNum = 1;
        speed = 0;
        maxSpeed = 100;
    }
}
