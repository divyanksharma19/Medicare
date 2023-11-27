import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.R
import java.util.Locale

class TimingAdapter(private val data: List<String>, private val context: Context) :
    RecyclerView.Adapter<TimingAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onClick(position: Int)
    }

    private var listener: OnClickListener? = null
    private var selectedPosition = RecyclerView.NO_POSITION // Initialize with no selection

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.timing_view_item)
        val textView: TextView = itemView.findViewById(R.id.texttiming)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.timing_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.textView.text = formatDate(item)

        // Set the background color based on the selection status
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#69BCE9")) // Selected color
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF")) // Default color
        }

        holder.cardView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            if (position == selectedPosition) {
                // Clicking on the already selected item, deselect it
                selectedPosition = RecyclerView.NO_POSITION // No item is selected
            } else {
                // Clicking on a new item, select it
                selectedPosition = position
            }
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)

            listener?.onClick(position)
        }
    }


    private fun formatDate(dateStr: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd EEE", Locale.getDefault())

        try {
            val date = inputFormat.parse(dateStr)
            if (date != null) {
                return outputFormat.format(date)
            }
        } catch (e: ParseException) {
            // Handle parsing exceptions if any
            e.printStackTrace()
        }

        return dateStr // Return the original date string in case of an error
    }


    override fun getItemCount(): Int {
        return data.size
    }
}
