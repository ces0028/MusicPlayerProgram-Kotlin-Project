package kr.or.mrhi.mymusicplayer

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.or.mrhi.mymusicplayer.MainActivity
import kr.or.mrhi.mymusicplayer.databinding.FragmentMainBinding
import kr.or.mrhi.mymusicplayer.databinding.FragmentPlaylistBinding

class PlayListFragment(val vibrator: Vibrator) : Fragment() {
    lateinit var binding: FragmentPlaylistBinding
    lateinit var playListAdapter: PlayListAdapter
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
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        notifyItem()
        return binding.root
    }

    fun notifyItem(){
        val dbConnector = DBConnector(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbConnector.selectMusicClicked("playList")

        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)
        binding.playListRecyclerView.layoutManager = linearLayoutManager
        playListAdapter = PlayListAdapter(mainContext.applicationContext, this, musicList, vibrator)
        binding.playListRecyclerView.adapter = playListAdapter
    }

    fun notifySearchItem(query: String?) {
        val dbConnector = DBConnector(mainContext, MainActivity.DB_NAME, MainActivity.VERSION)
        val musicList = dbConnector.selectSearchMusicClicked(query, "playList")
        val linearLayoutManager = LinearLayoutManager(mainContext.applicationContext)
        binding.playListRecyclerView.layoutManager = linearLayoutManager
        playListAdapter = PlayListAdapter(mainContext.applicationContext, this, musicList, vibrator)
        binding.playListRecyclerView.adapter = playListAdapter
    }
}