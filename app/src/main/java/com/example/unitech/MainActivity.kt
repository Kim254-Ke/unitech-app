package com.example.unitech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity: ", "MainActivity")
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close)

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            val handled = when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    Log.d("1111111111111: ", "1111111111")

                    drawerLayout.closeDrawer(navigationView)

                    true
                }
                R.id.exam_mgt -> {
                    Log.d("Navigation", "Exam Management clicked")
                    val intent = Intent(this, ExamManagementActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(navigationView)

                    true
                }

                R.id.announcements -> {
                    Log.d("Navigation", "Announcements clicked")
                    val intent = Intent(this, TeacherAnnouncementsActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(navigationView)

                    true
                }
                else -> false
            }
            handled
        }

        /////////////////////////////////////////////
        /////////////////////////////////////////////
        // Below is the code for the spinner

        val spinner:Spinner = findViewById(R.id.planets_spinner)
        spinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            this,
            R.array.planets_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
        /////////////////////////////////////////////////
        ////////////////////////////////////////////////

        progressBar = findViewById(R.id.progress_bar)
        val schoolPortalWebview: WebView = findViewById(R.id.school_portal_webview)
        schoolPortalWebview.loadUrl("https://portal.kisiiuniversity.ac.ke/")

        // The following is the appropriate url for openAI's chatgpt: "https://openai.com/"

        schoolPortalWebview.settings.javaScriptEnabled = true
        //schoolPortalWebview.webViewClient = WebViewClient()
        schoolPortalWebview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }


        schoolPortalWebview.canGoBack()
        schoolPortalWebview.setOnKeyListener(View.OnKeyListener{ v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action == MotionEvent.ACTION_UP
                && schoolPortalWebview.canGoBack()){
                schoolPortalWebview.goBack()
                return@OnKeyListener true
            }
            false
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else{
            super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long){
        Toast.makeText(this, "11111111111111111111111111111.", Toast.LENGTH_LONG).show()

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Toast.makeText(this, "44444444444444444444444444444444444.", Toast.LENGTH_LONG).show()

    }
    ///

}
