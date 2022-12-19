package kr.or.mrhi.mymusicplayer

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import kr.or.mrhi.mymusicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_READ = 80
        const val DB_NAME = "musicDB"
        var VERSION = 1
    }
    lateinit var binding: ActivityMainBinding
    lateinit var mainFragment: MainFragment
    lateinit var likeFragment: LikeFragment
    lateinit var playListFragment: PlayListFragment
    lateinit var vibrator: Vibrator
    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private var musicList: MutableList<Music>? = mutableListOf<Music>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_READ)
        }

        val pagerAdapter = PagerAdapter(this)
        val titleList = mutableListOf<String>("MAIN", "LIKE", "PLAYLIST")
        mainFragment = MainFragment(vibrator)
        likeFragment = LikeFragment(vibrator)
        playListFragment = PlayListFragment(vibrator)

        pagerAdapter.addFragment(mainFragment, titleList[0])
        pagerAdapter.addFragment(likeFragment, titleList[1])
        pagerAdapter.addFragment(playListFragment, titleList[2])

        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            tab.text = titleList.get(position)
        }.attach()
    }

    fun isPermitted(): Boolean {
        return ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startProcess()
        } else {
            Toast.makeText(this, "권한이 없어 프로그램이 종료됩니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun startProcess() {
        val dbConnector = DBConnector(this, DB_NAME, VERSION)
        musicList = dbConnector.selectMusicAll()

        if (musicList == null) {
            val playMusicList = getMusicList()
            if (playMusicList != null) {
                for(i in 0 .. playMusicList.size - 1) {
                    val music = playMusicList.get(i)
                    dbConnector.insertMusic(music)
                }
                musicList = playMusicList
            } else {
                Log.d("LogCheck", "MainActivity.startProcess() MUSIC DATA IS EMPTY")
            }
        }
    }

    fun getMusicList(): MutableList<Music>? {
        var getMusicList: MutableList<Music>? = mutableListOf<Music>()
        val musicURL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = contentResolver.query(musicURL, projection, null, null, null)
        if (cursor?.count!! > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val title = cursor.getString(1).replace("'", "")
                val artist = cursor.getString(2).replace("'", "")
                val albumId = cursor.getString(3)
                val duration = cursor.getInt(4)
                val music = Music(id, title, artist, albumId, duration, 0, 0)
                getMusicList?.add(music)
            }
        } else {
            getMusicList = null
        }
        return getMusicList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_overflow, menu)
        val dbConnector = DBConnector(applicationContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val searchMenu = menu?.findItem(R.id.menuSearch)
        val searchView = searchMenu?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(query.isNullOrBlank()) {
                    Log.d("LogCheck", "currentItem ${binding.viewPager.currentItem}")
                    musicList?.clear()
                    when(binding.viewPager.currentItem) {
                        0 -> mainFragment.notifyItem()
                        1 -> likeFragment.notifyItem()
                        2 -> playListFragment.notifyItem()
                    }
                } else {
                    musicList?.clear()
                    Log.d("LogCheck", "currentItem ${binding.viewPager.currentItem}")
                    when(binding.viewPager.currentItem) {
                        0 -> mainFragment.notifySearchItem(query)
                        1 -> likeFragment.notifySearchItem(query)
                        2 -> playListFragment.notifySearchItem(query)
                    }
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}