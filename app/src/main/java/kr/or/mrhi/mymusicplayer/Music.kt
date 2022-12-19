package kr.or.mrhi.mymusicplayer

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.io.IOException

@Parcelize
class Music(var id: String, var title: String?, var artist: String?, var albumId: String?, var duration: Int?, var likes: Int?, var playList: Int?) :
    Parcelable {

    companion object : Parceler<Music> {
        override fun create(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun Music.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(title)
            parcel.writeString(artist)
            parcel.writeString(albumId)
            parcel.writeInt(duration!!)
            parcel.writeInt(likes!!)
            parcel.writeInt(playList!!)
        }
    }
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    fun getAlbumUri(): Uri {
        return Uri.parse("content://media/external/audio/albumart/" + albumId)
    }

    fun getMusicUri(): Uri {
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    fun getAlbumImage(context: Context, albumImageSize: Int) : Bitmap? {
        val contentResolver: ContentResolver = context.contentResolver
        val uri = getAlbumUri()
        val options = BitmapFactory.Options()

        if (uri != null) {
            var parcelFileDescriptor: ParcelFileDescriptor? = null
            try {
                parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                var bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor!!.fileDescriptor, null, options)

                if (bitmap != null) {
                    val tempBitmap = Bitmap.createScaledBitmap(bitmap, albumImageSize, albumImageSize, true)
                    bitmap.recycle()
                    bitmap = tempBitmap
                }
                return bitmap
            } catch(e: Exception) {
                Log.d("kr.or.mrhi.study_221116", "getAlbumImage() ${e.printStackTrace()}")
            } finally {
                try {
                    parcelFileDescriptor?.close()
                } catch(e: IOException) {
                    Log.d("kr.or.mrhi.study_221116", "getAlbumImage() parcelFileDescriptor ${e.printStackTrace()}")
                }
            }
        }
        return null
    }
}