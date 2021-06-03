package app.beer.restaurant.ui.fragments.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.api.BASE_URL
import app.beer.restaurant.model.product.Product
import app.beer.restaurant.ui.fragments.show.ShowFragment
import app.beer.restaurant.util.*

open class MainAdapter(var data: ArrayList<Product>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val smallProductPhoto: ImageView = itemView.findViewById(R.id.small_product_photo)
        val smallProductName: TextView = itemView.findViewById(R.id.small_product_name)
        val smallProductPrice: TextView = itemView.findViewById(R.id.small_product_price)

        fun bind(product: Product) {
            smallProductPhoto.loadAndSetImage("$BASE_URL/${product.photoUrl}")

            smallProductName.text = when {
                APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_ENG -> {
                    smallProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price_USD)
                    product.name
                }
                APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_RUS -> {
                    smallProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price)
                    product.name_RU
                }
                APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_DOT -> {
                    smallProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price_EURO)
                    product.name_DE
                }
                else -> {
                    smallProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price_USD)
                    product.name_BG
                }
            }

            itemView.setOnClickListener {
                replaceFragment(ShowFragment.newInstance(product.id))
            }
            smallProductPhoto.setOnClickListener {
                replaceFragment(ShowFragment.newInstance(product.id))
            }
        }
    }

    fun addItems(items: List<Product>) {
        val position = data.size
        // data.clear()
        data.addAll(items)
        notifyItemRangeInserted(position, data.size)
    }

    fun setData(items: List<Product>) {
        data.clear()
        data = items as ArrayList<Product>
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = data[position]
        holder.bind(product)
    }

}