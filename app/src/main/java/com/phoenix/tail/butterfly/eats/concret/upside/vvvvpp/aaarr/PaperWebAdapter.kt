package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.aaarr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.wwwtt.CustomWebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.InputStream
import java.net.URL

class PaperWebAdapter(private var dataList: MutableList<CustomWebView.WebPageInfo>) :
    RecyclerView.Adapter<PaperWebAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(date: String)
    }

    interface OnItemSelectedListener {
        fun onItemSelected(date: String)
    }

    private var listener: OnItemClickListener? = null
    private var listenerselected: OnItemSelectedListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.listenerselected = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvUrl: TextView = itemView.findViewById(R.id.tv_url)
        val imgCheck: ImageView = itemView.findViewById(R.id.img_check)
        val imgIcon: ImageView = itemView.findViewById(R.id.img_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view =
            inflater.inflate(R.layout.lay_web_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        setVisibility(data.isGone, holder.itemView)
        holder.tvTitle.text = data.title
        holder.tvUrl.text = data.url
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = fetchFaviconTask(data.url)
            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    holder.imgIcon.setImageBitmap(bitmap)
                }
            }
        }
        if (data.selected) {
            holder.imgCheck.setImageResource(R.drawable.ic_check_x)
        } else {
            holder.imgCheck.setImageResource(R.drawable.ic_check)
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClick(data.date)
        }
        holder.imgCheck.setOnClickListener {
            listenerselected?.onItemSelected(data.date)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newData: MutableList<CustomWebView.WebPageInfo>) {
        dataList = newData
        notifyDataSetChanged()
    }
//    fun deleteData(position: Int,isHistory: Boolean) {
//        if(isHistory){
//            BVDataUtils.deleteWebPageHistory(dataList[position])
//        }else{
//            BVDataUtils.deleteWebPageBookmark(dataList[position])
//        }
//        dataList.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, dataList.size)
//    }

    private fun setVisibility(isVisible: Boolean, itemView: View) {
        val param = itemView.layoutParams as RecyclerView.LayoutParams
        if (!isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT
            param.width = LinearLayout.LayoutParams.MATCH_PARENT
            itemView.visibility = View.VISIBLE
        } else {
            itemView.visibility = View.GONE
            param.height = 0
            param.width = 0
        }
        itemView.layoutParams = param
    }

    private fun fetchFaviconTask(url: String): Bitmap? {
        return try {
            val doc = Jsoup.connect(url).get()
            val iconUrl = doc.head()
                .select("link[rel=icon], link[rel^=shortcut icon], link[rel^=apple-touch-icon]")
                .first()
                ?.absUrl("href")
                ?: URL(URL(url), "/favicon.ico").toString()

            val inputStream: InputStream = URL(iconUrl).openStream()
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
}