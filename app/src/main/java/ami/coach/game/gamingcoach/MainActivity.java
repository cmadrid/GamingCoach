package ami.coach.game.gamingcoach;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ami.coach.game.gamingcoach.database.DBJuego;
import layout.games;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    static Intent intent;
    static String steamId64;
    static String steamId;
    static SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        this.intent = getIntent();


        sharedpreferences = getSharedPreferences(RegistroActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        steamId64 = sharedpreferences.getString(RegistroActivity.Prefs.SteamId64.name(), "");
        steamId = sharedpreferences.getString(RegistroActivity.Prefs.SteamId.name(), "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                      .setAction("Action", null).show();
          }
      });


        ImageView imagen_perfil = (ImageView)findViewById(R.id.img_perfil);
        TextView steam_id = (TextView)findViewById(R.id.steam_id);
        TextView estado = (TextView)findViewById(R.id.status);
        TextView mensaje_estado = (TextView)findViewById(R.id.stateMessage);
        TextView actualizado = (TextView)findViewById(R.id.updated);



        String estado_str = sharedpreferences.getString(RegistroActivity.Prefs.StateMessage.name(), "");
        String id = sharedpreferences.getString(RegistroActivity.Prefs.SteamId.name(), "");
        String id64 = sharedpreferences.getString(RegistroActivity.Prefs.SteamId64.name(), "");
        String customUrl = sharedpreferences.getString(RegistroActivity.Prefs.CustomUrl.name(), "");
        String estadoEnlinea = sharedpreferences.getString(RegistroActivity.Prefs.OnlineState.name(), "");

        steam_id.setText(id);
        estado.setText(estadoEnlinea);
        mensaje_estado.setText(estado_str);
        imagen_perfil.setImageURI(Uri.parse(sharedpreferences.getString(RegistroActivity.Prefs.Avatar.name(), null)));
        
        Long millis = sharedpreferences.getLong(RegistroActivity.Prefs.updated.name(), 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        String newstring = new SimpleDateFormat("dd/MM/yyy HH:mm").format(calendar.getTime());

        actualizado.setText("Actualizado el: " + newstring);
        
        if(estadoEnlinea.equalsIgnoreCase("offline"))
            estado.setTextColor(Color.RED);
        else
            estado.setTextColor(Color.GREEN);



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrar_sesion) {
            SharedPreferences sharedpreferences = getSharedPreferences(RegistroActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.putString(RegistroActivity.Prefs.CustomUrl.name(), sharedpreferences.getString(RegistroActivity.Prefs.CustomUrl.name(), null));
            editor.commit();

            Intent intent = new Intent(this,RegistroActivity.class);
            startActivity(intent);
            this.finish();

            DBJuego db_juego=new DBJuego(this);
            db_juego.vaciar();
            db_juego.close();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0)
                return games.newInstance();//
            else return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Actididades";
                case 1:
                    return "Estadisticas";
                case 2:
                    return "Proximamente";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
