package kr.or.mrhi.mymusicplayer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kr.or.mrhi.mymusicplayer.databinding.ItemMusicBinding
import java.text.SimpleDateFormat

class LikeAdapter(val context: Context, val likeFragment: LikeFragment, val musicList: MutableList<Music>?, val vibrator: Vibrator) : RecyclerView.Adapter<LikeAdapter.CustomViewHolder>() {
    companion object {
        const val ALBUM_SIZE = 280
    }

    override fun getItemCount(): Int {
        return musicList?.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = (holder as CustomViewHolder).binding
        val music = musicList?.get(position)

        binding.tvArtist.text = music?.artist
        binding.tvArtist.isSelected = true
        binding.tvTitle.text = music?.title
        binding.tvTitle.isSelected = true
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
        val bitmap = context.let { music?.getAlbumImage(it, ALBUM_SIZE) }
        Log.d("LogCheck", "${bitmap}")
        if (bitmap != null) {
            binding.ivAlbumImage.setImageBitmap(bitmap)
        } else {
            binding.ivAlbumImage.setImageResource(R.drawable.ic_default)
        }

        when(music?.likes) {
            0 -> binding.ivItemLike.setImageResource(R.drawable.ic_like_empty)
            1 -> binding.ivItemLike.setImageResource(R.drawable.ic_like_full)
        }

        when(music?.playList) {
            0 -> binding.ivItemPlayList.setImageResource(R.drawable.ic_playlist_empty)
            1 -> binding.ivItemPlayList.setImageResource(R.drawable.ic_playlist_full)
        }

        binding.root.setOnClickListener {
            val playList: ArrayList<Parcelable> = musicList as ArrayList<Parcelable>
            val intent = Intent(binding.root.context, PlayActivity::class.java)
            intent.putExtra("playList", playList)
            intent.putExtra("position", position)
            intent.putExtra("music", music)
            binding.root.context.startActivity(intent)
        }

        binding.ivItemLike.setOnClickListener {
            vibrator.vibrate(VibrationEffect.createOneShot(50, 10))
            if(music?.likes == 0) {
                binding.ivItemLike.setImageResource(R.drawable.ic_like_full)
                music.likes = 1
            } else {
                binding.ivItemLike.setImageResource(R.drawable.ic_like_empty)
                music?.likes = 0
            }

            if(music != null) {
                val dbHelper = DBConnector(context, MainActivity.DB_NAME, MainActivity.VERSION)
                val flag = dbHelper.updateIcon(music)
                if (flag) {
                    likeFragment.notifyItem()
                } else {
                    Log.d("LogCheck", "MusicRecyclerAdapter.onBindViewHolder() ${music.id} UPDATE ERROR")
                }
            }
        }

        binding.ivItemPlayList.setOnClickListener {
            vibrator.vibrate(VibrationEffect.createOneShot(50, 10))
            if(music?.playList == 0) {
                binding.ivItemPlayList.setImageResource(R.drawable.ic_playlist_full)
                music.playList = 1
            } else {
                binding.ivItemPlayList.setImageResource(R.drawable.ic_playlist_empty)
                music?.playList = 0
            }

            if(music != null) {
                val dbConnector = DBConnector(context, MainActivity.DB_NAME, MainActivity.VERSION)
                val flag = dbConnector.updateIcon(music)
                if (!flag) {
                    Log.d("LogCheck", "MusicRecyclerAdapter.onBindViewHolder() ${music.id} UPDATE ERROR")
                }
            }
        }
    }
    class CustomViewHolder(val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root)
}