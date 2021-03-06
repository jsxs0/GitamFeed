package com.gfeed.sudhaseshu.gitamfeed;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Notice.OnFragmentInteractionListener,Discussion.OnFragmentInteractionListener{

    public FirebaseAuth mAuth;
    public TextView usrnm,emaild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar ..
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Gitam Feed");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));


        //Navigation View
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View n = navigationView.getHeaderView(0);

        //drawer Layout
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //TabLayouts ..

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Notice"));
        tabLayout.addTab(tabLayout.newTab().setText("Discussions"));

        if (haveNetworkConnection()) {
            try {
                mAuth = FirebaseAuth.getInstance();
                Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(getResources().getColor(R.color.white));


                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                if (acct != null) {
                    String name = acct.getDisplayName();
                    String mail = acct.getEmail();
                    usrnm = n.findViewById(R.id.userName);
                    emaild = n.findViewById(R.id.emailI);
                    usrnm.setText(name);
                    emaild.setText(mail);
                }


                final ViewPager viewPager = findViewById(R.id.pager);

                final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
                viewPager.setAdapter(adapter);

                viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout)); //Deprecated but needed

                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
            catch (Exception e){
                noNetworkAction();
            }
        }

        else {
            noNetworkAction();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details?id=com.gfeed.sudhaseshu.gitamfeed");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

//    public void bookmark()
//    {
//
//        Bookmark_Fragment fragment1 = new Bookmark_Fragment();
//        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.bookmark_recyclerview,fragment1);
//        transaction.commit();
//
//    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.bookmarks:
                Intent intent = new Intent(this,BookmarkActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_slideshow:
                Intent intent2 = new Intent(this,Yourposts.class);
                startActivity(intent2);
                break;
            case R.id.nav_manage:
                Uri uriUrl = Uri.parse("https://drive.google.com/drive/folders/1t277sx-l3PA4ZCaA6IW53O6Vwxf4QDTG?usp=sharing");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                break;

            case R.id.logout_nav:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;


            case R.id.about:
                Intent intent1 = new Intent(this,About.class);
                startActivity(intent1);
                break;
            case R.id.rate:
                rateApp();
                break;
        }
        return false;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void noNetworkAction(){
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                "No internet connection.",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(ContextCompat.getColor(Objects.requireNonNull(getApplicationContext()),
                R.color.black));
        snackbar.setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recheck internet connection and call DownloadJson if there is internet
                if(! haveNetworkConnection())
                    noNetworkAction();
                else {
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                }
            }
        }).show();
    }

}
