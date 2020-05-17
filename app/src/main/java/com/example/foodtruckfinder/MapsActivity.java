package com.example.foodtruckfinder;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MapsActivity extends FragmentActivity implements View.OnClickListener{

    private LinearLayout xian_cuisine, pako_takos, quickbite, halal_grill, tasty_kabob, restaurant_list;
    private Spinner spinner;
    private EditText search;
    private ToggleButton xian_cuisin_fav, pakos_tako_fav, quickbite_fav, halal_grill_fav, tasty_kabob_fav;

    private ScaleAnimation scaleAnimation;
    private BounceInterpolator bounceInterpolator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_page);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(filterPage);

//        parseJSON();
//        System.out.println(names);
//        System.out.println(styles);

        restaurant_list = (LinearLayout) findViewById(R.id.restaurant_list);

        xian_cuisine = (LinearLayout) findViewById(R.id.xian_cuisine);
        xian_cuisine.setOnClickListener(this);

        pako_takos = (LinearLayout) findViewById(R.id.pakos_takos);
        pako_takos.setOnClickListener(this);

        quickbite = (LinearLayout) findViewById(R.id.quickbite);
        quickbite.setOnClickListener(this);

        halal_grill = (LinearLayout) findViewById(R.id.halal_grill);
        halal_grill.setOnClickListener(this);

        tasty_kabob = (LinearLayout) findViewById(R.id.tasty_kabob);
        tasty_kabob.setOnClickListener(this);

        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(searchPage);

        //Toggle button animation effect
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        xian_cuisin_fav = (ToggleButton) findViewById(R.id.xian_cuisine_fav);
        xian_cuisin_fav.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                       //animation
                       compoundButton.startAnimation(scaleAnimation);
                   }
               });
        pakos_tako_fav = (ToggleButton) findViewById(R.id.pakos_takos_fav);
        pakos_tako_fav.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        //animation
                        compoundButton.startAnimation(scaleAnimation);
                    }
                });
        quickbite_fav = (ToggleButton) findViewById(R.id.quickbite_fav);
        quickbite_fav.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        //animation
                        compoundButton.startAnimation(scaleAnimation);
                    }
                });
        halal_grill_fav = (ToggleButton) findViewById(R.id.halal_grill_fav);
        halal_grill_fav.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        //animation
                        compoundButton.startAnimation(scaleAnimation);
                    }
                });
        tasty_kabob_fav = (ToggleButton) findViewById(R.id.tasty_kabob_fav);
        tasty_kabob_fav.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        //animation
                        compoundButton.startAnimation(scaleAnimation);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        // handling onClick Events
        switch (v.getId()) {
            case R.id.xian_cuisine:
                goTo("xian_cuisine");
                break;
            case R.id.pakos_takos:
                goTo("pakos_takos");
                break;
            case R.id.quickbite:
                goTo("quickbite");
                break;
            case R.id.halal_grill:
                goTo("halal_grill");
                break;
            case R.id.tasty_kabob:
                goTo("tasty_kabob");
                break;
        }
    }

    private void goTo(String restaurant) {
        Intent intent = new Intent(MapsActivity.this, SecondActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private TextWatcher searchPage = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String input = s.toString().toLowerCase();
            for (int i = 0; i < restaurant_list.getChildCount(); i++) {
                LinearLayout v = (LinearLayout) restaurant_list.getChildAt(i);

                v.setVisibility(LinearLayout.VISIBLE);

                LinearLayout text = (LinearLayout) ((LinearLayout) v.getChildAt(1));
                String name = ((TextView) text.getChildAt(1)).getText().toString().toLowerCase();
                String style = ((TextView) text.getChildAt(2)).getText().toString().toLowerCase();

                if (!name.contains(input) && !style.equals(input)) {
                    v.setVisibility(LinearLayout.GONE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    };

    private AdapterView.OnItemSelectedListener filterPage = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // hide selection text
            ((TextView)view).setText(null);
            // if you want you can change background here

            String input = String.valueOf(spinner.getSelectedItem()).toLowerCase();
            if (input.equals("all")) {
                for (int i = 0; i < restaurant_list.getChildCount(); i++) {
                    LinearLayout v = (LinearLayout) restaurant_list.getChildAt(i);
                    v.setVisibility(LinearLayout.VISIBLE);
                }
            } else if (input.equals("favorite")) {
                for (int i = 0; i < restaurant_list.getChildCount(); i++) {
                    LinearLayout v = (LinearLayout) restaurant_list.getChildAt(i);
                    v.setVisibility(LinearLayout.GONE);
                }
                if (xian_cuisin_fav.isChecked()) xian_cuisine.setVisibility(LinearLayout.VISIBLE);
                if (pakos_tako_fav.isChecked()) pako_takos.setVisibility(LinearLayout.VISIBLE);
                if (quickbite_fav.isChecked()) quickbite.setVisibility(LinearLayout.VISIBLE);
                if (halal_grill_fav.isChecked()) halal_grill.setVisibility(LinearLayout.VISIBLE);
                if (tasty_kabob_fav.isChecked()) tasty_kabob.setVisibility(LinearLayout.VISIBLE);

            } else {
                for (int i = 0; i < restaurant_list.getChildCount(); i++) {
                    LinearLayout v = (LinearLayout) restaurant_list.getChildAt(i);

                    v.setVisibility(LinearLayout.VISIBLE);

                    LinearLayout text = (LinearLayout) ((LinearLayout) v.getChildAt(1));
                    String style = ((TextView) text.getChildAt(2)).getText().toString().toLowerCase();

                    if (!style.equals(input)) {
                        v.setVisibility(LinearLayout.GONE);
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
