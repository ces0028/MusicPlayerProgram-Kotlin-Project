package kr.or.mrhi.mymusicplayer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kr.or.mrhi.mymusicplayer.databinding.ActivityLoadBinding

class LoadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this).load(R.raw.gif_load).override(500, 500).into(binding.ivLoad)
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()},
            3000)
    }
}