package com.example.pdfimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.ls.LSInput;

import java.lang.reflect.Type;
import java.net.Proxy;
import java.security.KeyRep;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.showPageNumber,GotoDialog.gotoInterface,BookmarDialog.gotoInterface,SaveBookmarkDialog.sendTitleInterface{
    PinchRecyclerView recyclerView;

    List<ImageList> dataList = new ArrayList<>();
    RecyclerView.SmoothScroller smoothScroller;
    LinearLayoutManager layoutManager,myLayout,myManager;
    SharedPreferences sharedPreferences,sharedPreferences1;
    public static final String TAG = "Basic";
    private int temp;
    private int uiOptins;
    String string;
    private boolean backPressed = false;
    boolean isImmersiveModeEnabled;
    Integer getLastviewed;
    TextView textView;
    FloatingActionButton menu1,menu2;
    FloatingActionMenu floatingActionMenu;
    Integer myPos,bookmarkedPage,bmk;
    RecyclerView.OnScrollListener listener;
    public static final long DISCONNECT_TIMEOUT = 6000;
    List<BookmarkItem> bookmarkItemList = new ArrayList<>();
    String bookmarTitle;
    //SaveBookmarkDialog saveBookmarkDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fillData();
        uiOptins =getWindow().getDecorView().getSystemUiVisibility();

        recyclerView = findViewById(R.id.recyclerView);
        menu1 = findViewById(R.id.subFloatingMenu1);
        menu2 = findViewById(R.id.subFloatingMenu2);
        floatingActionMenu = findViewById(R.id.FloatingActionMenu1);
        textView = findViewById(R.id.myPage);
        textView.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //add smooth scroller
        smoothScroller = new LinearSmoothScroller(this){
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        RecyclerAdapter adapter = new RecyclerAdapter(dataList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //to hide app bar
       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isScrolled){
                    if (dy>0)
                    {
                        toggleHideyBar();
                        isScrolled = true;
                    }
                }else
                {
                    if (dy<0){
                        showBar();
                        isScrolled = false;
                    }
                }
            }
        });
       */ //to get to last viewed page
        sharedPreferences = getSharedPreferences("LastViewed",MODE_PRIVATE);
        getLastviewed = sharedPreferences.getInt("lastviewedPage",0);
        bookmarkedPage = sharedPreferences.getInt("bookmarkedPage",0);
        Log.d(TAG, "onCreate: bmk retrieved" + bookmarkedPage);
        Log.d(TAG, "retrieved position :" + getLastviewed);
        if (getLastviewed!=null&&getLastviewed>0)
        {
            Log.d(TAG, "before");
            smoothScroller.setTargetPosition(getLastviewed);
            layoutManager.startSmoothScroll(smoothScroller);
            Log.d(TAG, "after");

        }
        /*listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    myManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                    int myPos = myManager.findFirstVisibleItemPosition()+1;
                    textView.setText(myPos + "");
                }
            }
        };
        recyclerView.addOnScrollListener(listener);
        *///floating menu
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final int bookmark = myLayout.findFirstVisibleItemPosition();
                openSaveBookmarkDialog();
                Log.d(TAG, "onClick: title" + bookmarTitle) ;
                //if (bookmarTitle!=null)
                {
                    /*SharedPreferences.Editor editor = getSharedPreferences("LastViewed",MODE_PRIVATE).edit();
                    bookmarkItemList.add(new BookmarkItem(bookmarTitle,bookmark));
                    Gson gson = new Gson();
                    String json = gson.toJson(bookmarkItemList);
                    editor.putString("bookmarkedPageList",json);
                    editor.apply();*/
                }
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        //set ui change listener
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            uiOptins = getWindow().getDecorView().getSystemUiVisibility();
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls
                            // .
                            getSupportActionBar().show();
                            floatingActionMenu.setVisibility(View.VISIBLE);

                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.

                        }
                    }
                });
        myLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
        //to get the bookmarked_list
        sharedPreferences1 = getSharedPreferences("LastViewed",MODE_PRIVATE);
        string = sharedPreferences.getString("bookmarkedPageList","[]");
        Gson gson1 = new Gson();
        Type type = new TypeToken<List<BookmarkItem>>(){}.getType();
        bookmarkItemList =gson1.fromJson(string,type);
        //bookmarkItemList.add(new BookmarkItem("bookmark 1",2));
        //bookmarkItemList.add(new BookmarkItem("bookmark 2",4));
    }

    //to automatic hide the bar and fab button

    private Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // todo
            return true;
        }
    });

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            HideSystemBars();
            floatingActionMenu.setVisibility(View.GONE);
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        int firstPos = myLayout.findFirstVisibleItemPosition();
        SharedPreferences.Editor editor = getSharedPreferences("LastViewed",MODE_PRIVATE).edit();
        Log.d(TAG, "onStop: "+firstPos);
        editor.putInt("lastviewedPage",(firstPos));
        editor.commit();
        stopDisconnectTimer();
        backPressed = false;
        //recyclerView.removeOnScrollListener(listener);
    }

    void  fillData(){
        String baseUrl = "http://twowaits.in/images/notes/B.Tech_.-SEMESTER-WISE-COURSE-STRUCTURE-ALL-TECHNOLOGY-BRANCHES_page-00";
        for (int i=1;i<40;i++){
            if (i<10){
                dataList.add(new ImageList(baseUrl+"0"+i+".jpg"));
            }
            else {
                dataList.add(new ImageList(baseUrl+i+".jpg"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.bookmark :
                openBookmarkDialog();
                return true;
            case R.id.share :
                HideSystemBars();
                Toast.makeText(this,"Share Document",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.report :

                Toast.makeText(this,"Report Document",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.upload :
                Intent intent = new Intent(this,UploadActivity.class);
                startActivity(intent);
            }

        return false;
    }

    @Override
    public void showPage(int position) {
        myPos = position;
    }

    /*public void toggleHideyBar() {
        int uiOptions =getWindow().getDecorView().getSystemUiVisibility();
        temp = uiOptions;
        int newUiOptions = uiOptions;
        isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE) == uiOptions);
        Log.d(TAG, "toggleHideyBar: " + isImmersiveModeEnabled);
        {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE;
            getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            getSupportActionBar().hide();
        }

    }
    */public void HideSystemBars(){
        int options =getWindow().getDecorView().getSystemUiVisibility();

        int newUiOptions;
        if (options==uiOptins){
            Log.d(TAG, "HideSystemBars: " + "Not is fullscreen");
            newUiOptions = options;
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE;
            getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            getSupportActionBar().hide();
        }
    }
    /*void showBar()
    {
        getWindow().getDecorView().setSystemUiVisibility(temp);
        getSupportActionBar().show();
    }*/

    public  void openDialog(){
        GotoDialog dialog = new GotoDialog();
        dialog.show(getSupportFragmentManager(),"MyDialog");

    }
    public void openBookmarkDialog()
    {
       sharedPreferences1 = getSharedPreferences("LastViewed",MODE_PRIVATE);
       String s = sharedPreferences.getString("bookmarkedPageList","{}");
        Log.d(TAG, "openBookmarkDialog: "+s);
        if (s.equals("{}")){
            Toast.makeText(this,"No Bookmarks Available",Toast.LENGTH_SHORT).show();
        }else{
           Gson gson1 = new Gson();
            Type type = new TypeToken<List<BookmarkItem>>(){}.getType();
            List<BookmarkItem> myList = (List<BookmarkItem>) gson1.fromJson(s,type);
            BookmarDialog bookmarDialog = new BookmarDialog(myList);
            bookmarDialog.show(getSupportFragmentManager(),"Bookmark Dialog");
        }
    }
    public void openSaveBookmarkDialog()
    {
        int sendPos = myLayout.findFirstVisibleItemPosition();
        SaveBookmarkDialog saveBookmarkDialog =new SaveBookmarkDialog(sendPos,bookmarkItemList);
        saveBookmarkDialog.show(getSupportFragmentManager(),"Save Dialog");
    }
// Interface Methods
    @Override
    public void jumpto(int position) {
        int total = recyclerView.getAdapter().getItemCount();
        if (position>total)
        {
            Toast.makeText(this,"Invalid Index",Toast.LENGTH_SHORT).show();
        }
        else {
            smoothScroller.setTargetPosition(position-1);
            layoutManager.startSmoothScroll(smoothScroller);
        }

    }
    @Override
    public void bookmarkJumpto(int position) {
        Log.d(TAG, "bookmarkJumpto: " + position);
        smoothScroller.setTargetPosition(position);
        layoutManager.startSmoothScroll(smoothScroller);
    }
    // to get the bookmark Title
    @Override
    public void sendTitle(String title) {
       bookmarTitle = title;
    }

    @Override
    public void onBackPressed() {
        if (!backPressed) {
            Toast.makeText(this,"Press again to exit.",Toast.LENGTH_SHORT).show();
        }
        if (backPressed){
            super.onBackPressed();
        }
        backPressed = true;
    }
}
