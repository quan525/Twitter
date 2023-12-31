package vn.edu.usth.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    TextView textViewUserName,textViewUserTagname;

    String userEmail;

    CircleImageView profileAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance("https://twitterauthentication-453e4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userRef = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userEmail = currentUser.getEmail();
        textViewUserName = findViewById(R.id.profile_user_name);
        textViewUserTagname = findViewById(R.id.profile_user_tagname);
        profileAvatar = findViewById(R.id.avatarNavView);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String dbEmail = dataSnapshot.child("email").getValue(String.class);
                            if (dbEmail != null && dbEmail.equals(userEmail)) {
                                String userName = dataSnapshot.child("name").getValue(String.class);
                                String userTagname = dataSnapshot.child("tagName").getValue(String.class);
                                String profileImageReference = dataSnapshot.child("AvatarImage").getValue(String.class);
                                // Set the TextViews with user data
                                runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      Picasso.get().load(profileImageReference).into(profileAvatar);
                                                      textViewUserName.setText(userName);
                                                      textViewUserTagname.setText(userTagname);
                                                  }
                                              });
                                // You may break out of the loop since you found the user
                                break;
                            }
                        }
                    }
                }).start();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        PagerAdapter adapter = new HomeFragmentPagerAdapter(
                getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(5);
        pager.setAdapter(adapter);

        TabLayout tabLayout =  findViewById(R.id.header);
        tabLayout.setupWithViewPager(pager);
        profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, AvatarActivity.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editProfile) {
            Intent intent = new Intent(this, UpdateProfile.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 5;
        private final String[] titles = new String[] { "Post", "Replies", "Highlight", "Media", "Likes" };
        public HomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return PAGE_COUNT; // number of pages for a ViewPager
        }
        @Override
        public Fragment getItem(int page) {
// returns an instance of Fragment corresponding to the specified page
            switch (page) {
                case 0: return new vn.edu.usth.twitter.PostProfileFragment();
                case 1: return new vn.edu.usth.twitter.RepliesProfileFragment();
                case 2: return new vn.edu.usth.twitter.PostProfileFragment();
                case 3: return new vn.edu.usth.twitter.MediaProfileFragment();
                case 4: return new vn.edu.usth.twitter.LikesProfileFragment();
            }
            return new Fragment(); // failsafe
        }
        @Override
        public CharSequence getPageTitle(int page) {
// returns a tab title corresponding to the specified page
            return titles[page];
        }
    }



}
