package kr.or.mrhi.mymusicplayer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import kotlinx.coroutines.*
import kr.or.mrhi.mymusicplayer.databinding.ActivityPlayBinding
import java.text.SimpleDateFormat

class PlayActivity : AppCompatActivity() {
    companion object {
        val ALBUM_SIZE = 280
        val DEFAULT_TIME = "00:00"
    }
    private lateinit var binding: ActivityPlayBinding
    private var playList: MutableList<Parcelable>? = null
    private var position: Int = 0
    private var bitmap: Bitmap? = null
    private var music: Music? = null
    private var mediaPlayer: MediaPlayer? = null
    private var messengerJob: Job? = null
    private var clickLike = false
    private var clickPlayList = false
    private var clickReplay = false
    private var clickShuffle = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        playList = intent.getParcelableArrayListExtra("playList")
        position = intent.getIntExtra("position", 0)
        music = playList?.get(position) as Music
        setMusic(music)
        playMusic()

        binding.ivList.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()
            finish()
            saveIcon(music)
        }

        binding.ivPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.ivPlay.setImageResource(R.drawable.ic_play)
            } else {
                playMusic()
            }
        }

        binding.ivPrevious.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()
            binding.seekBar.progress = 0
            binding.playDuration.text = DEFAULT_TIME

            val previousPosition = position - 1
            if (previousPosition < 0) {
                position = playList!!.size - 1
                music = playList?.get(position) as Music
            } else {
                position = previousPosition
                music = playList?.get(position) as Music
            }

            if (clickShuffle) {
                val plus = (Math.random() * playList?.size!! + 1).toInt()
                position += plus
                if (position > playList?.size!!) {
                    position -= playList?.size!!
                }
            }
            music = playList?.get(position) as Music
            setMusic(music)
            playMusic()
            saveIcon(music)
        }

        binding.ivNext.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()
            binding.seekBar.progress = 0
            binding.playDuration.text = DEFAULT_TIME

            val nextPosition = position + 1
            if (nextPosition == playList?.size!!) {
                position = 0
                music = playList?.get(position) as Music
            } else {
                position = nextPosition
                music = playList?.get(position) as Music
            }

            if (clickShuffle) {
                val plus = (Math.random() * (playList?.size!! + 1)).toInt()
                position += plus
                if (position > playList?.size!!) {
                    position -= playList?.size!!
                }
            }
            music = playList?.get(position) as Music
            setMusic(music)
            playMusic()
            saveIcon(music)
        }

        binding.ivLike.setOnClickListener {
            vibrator.vibrate(VibrationEffect.createOneShot(50, 10))
            val image = binding.ivLike
            clickLike = if (clickLike) {
                image.setImageResource(R.drawable.ic_like_empty)
                false
            } else {
                image.setImageResource(R.drawable.ic_like_full)
                true
            }
            saveIcon(music)
        }

        binding.ivPlayList.setOnClickListener {
            vibrator.vibrate(VibrationEffect.createOneShot(50, 10))
            val image = binding.ivPlayList
            clickPlayList = if (clickPlayList) {
                image.setImageResource(R.drawable.ic_playlist_empty)
                false
            } else {
                image.setImageResource(R.drawable.ic_playlist_full)
                true
            }
            saveIcon(music)
        }

        binding.ivReplay.setOnClickListener {
            val image = binding.ivReplay
            if (clickShuffle) {
                clickShuffle = false
                binding.ivShuffle.setImageResource(R.drawable.ic_shuffle_empty)
            }
            if (clickReplay) {
                image.setImageResource(R.drawable.ic_replay_empty)
                clickReplay = false
            } else {
                image.setImageResource(R.drawable.ic_replay_full)
                clickReplay = true
            }
        }

        binding.ivShuffle.setOnClickListener {
            val image = binding.ivShuffle
            if (clickReplay) {
                clickReplay = false
                binding.ivReplay.setImageResource(R.drawable.ic_replay_empty)
            }

            if (clickShuffle) {
                image.setImageResource(R.drawable.ic_shuffle_empty)
                clickShuffle = false
            } else {
                image.setImageResource(R.drawable.ic_shuffle_full)
                clickShuffle = true
            }
        }
    }

    fun playMusic() {
        mediaPlayer?.start()
        binding.ivPlay.setImageResource(R.drawable.ic_pause)

        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
        messengerJob = backgroundScope.launch {
            while (mediaPlayer?.isPlaying == true) {

                runOnUiThread {
                    val currentPosition = mediaPlayer?.currentPosition!!
                    binding.seekBar.progress = currentPosition
                    val currentDuration = SimpleDateFormat("mm:ss").format(mediaPlayer!!.currentPosition)
                    binding.playDuration.text = currentDuration
                }
                try {
                    delay(1000)
                } catch (e: Exception) {
                    Log.d("kr.or.mrhi.musicplayer", "Thread Error")
                }
            }

            runOnUiThread {
                if (mediaPlayer!!.currentPosition >= (binding.seekBar.max - 1000)) {
                    binding.seekBar.progress = 0
                    binding.playDuration.text = DEFAULT_TIME
                    if (clickShuffle) {
                        val plus = (Math.random() * playList?.size!!).toInt()
                        position += plus
                        if (position > playList?.size!!) {
                            position -= playList?.size!!
                        }
                    } else if (!clickReplay) {
                        if (position == playList?.size!! -1) {
                            position = 0
                        } else {
                            position += 1
                        }
                    }
                    music = playList?.get(position) as Music
                    setMusic(music)
                    playMusic()
                }
            }
        }
    }

    fun saveIcon(music: Music?) {
        var flag = false
        music?.likes = if(clickLike) 1 else 0
        music?.playList = if(clickPlayList) 1 else 0
        if (music != null) {
            val dbConnector = DBConnector(this, MainActivity.DB_NAME, MainActivity.VERSION)
            flag = dbConnector.updateIcon(music)
            if (!flag) {
                Log.d("kr.or.mrhi","MusicRecyclerAdapter.onBindViewHolder() ${music.id} UPDATE ERROR")
            }
        }
    }

    fun setMusic(music: Music?) {
        binding.tvTitle.text = music?.title
        binding.tvTitle.isSelected = true
        binding.tvArtist.text = music?.artist
        binding.totalDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
        binding.playDuration.text = DEFAULT_TIME
        bitmap = music?.getAlbumImage(this, ALBUM_SIZE)
        if (bitmap != null) {
            binding.ivAlbumImage.setImageBitmap(bitmap)
        } else {
            binding.ivAlbumImage.setImageResource(R.drawable.ic_default)
        }
        if (music?.likes == 0) {
            binding.ivLike.setImageResource(R.drawable.ic_like_empty)
            clickLike = false
        } else {
            binding.ivLike.setImageResource(R.drawable.ic_like_full)
            clickLike = true
        }
        if (music?.playList == 0) {
            binding.ivPlayList.setImageResource(R.drawable.ic_playlist_empty)
            clickPlayList = false
        } else {
            binding.ivPlayList.setImageResource(R.drawable.ic_playlist_full)
            clickPlayList = true
        }

        mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
        binding.seekBar.max = mediaPlayer?.duration!!
        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("kr.or.mrhi", "onStartTrackingTouch()")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("kr.or.mrhi", "onStopTrackingTouch()")
            }
        })
    }

    override fun onBackPressed() {
        mediaPlayer?.stop()
        messengerJob?.cancel()
        finish()
    }
}