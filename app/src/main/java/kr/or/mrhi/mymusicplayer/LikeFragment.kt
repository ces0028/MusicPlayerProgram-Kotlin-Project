package kr.or.mrhi.mymusicplayer

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.or.mrhi.mymusicplayer.MainActivity
import kr.or.mrhi.mymusicplayer.databinding.FragmentLikeBinding
import kr.or.mrhi.mymusicplayer.databinding.FragmentMainBinding

class LikeFragment(val vibrator: Vibrator) : Fragment() {
    lateinit var binding: FragmentLikeBinding
    lateinit var likeAdapter : LikeAdapter
    lateinit var mainContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainContext = context
    }

    override fun onResume() {
        super.onResume()
        notifyItem()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLikeBinding.inflate(inflater, container, false)
        notifyItem()
        return binding.root
    }

    fun notifyItem() {
        val dbConnector = DBConnector(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbConnector.selectMusicClicked("likes")
        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)
        binding.likeRecyclerView.layoutManager = linearLayoutManager
        likeAdapter = LikeAdapter(mainContext.applicationContext, this, musicList, vibrator)
        binding.likeRecyclerView.adapter = likeAdapter
    }

    fun notifySearchItem(query: String?) {
        val dbConnector = DBConnector(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbConnector.selectSearchMusicClicked(query, "likes")
        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)
        binding.likeRecyclerView.layoutManager = linearLayoutManager
        likeAdapter = LikeAdapter(mainContext.applicationContext, this, musicList, vibrator)
        binding.likeRecyclerView.adapter = likeAdapter
    }
}