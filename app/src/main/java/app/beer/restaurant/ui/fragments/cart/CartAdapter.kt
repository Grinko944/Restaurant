package app.beer.restaurant.ui.fragments.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.beer.restaurant.R
import app.beer.restaurant.api.BASE_URL
import app.beer.restaurant.model.basket.Basket
import app.beer.restaurant.util.*

class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var data: ArrayList<Basket> = ArrayList()

    private lateinit var listener: OnClickListener

    interface OnClickListener {
        fun onClickDelete(product: Basket)
        fun onClickPlus(product: Basket)
        fun onClickMinus(product: Basket)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartProductImage: ImageView = itemView.findViewById(R.id.cart_product_image)
        val cartProductName: TextView = itemView.findViewById(R.id.cart_product_name)
        val cartProductPrice: TextView = itemView.findViewById(R.id.cart_product_price)
        val deleteCartItem: ImageView = itemView.findViewById(R.id.delete_cart_item)

        val productCountText: TextView = itemView.findViewById(R.id.product_count_text)
//        val plusProductCount: ImageView = itemView.findViewById(R.id.plus_product_count)
//        val minusProductCount: ImageView = itemView.findViewById(R.id.minus_product_count)

        fun bind(product: Basket) {
            cartProductImage.loadAndSetImage(BASE_URL + product.photoUrl)
            cartProductName.text = when {
                APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_ENG -> {
                    cartProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price_USD)
                    product.name
                }
                APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_RUS -> {
                    cartProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price)
                    product.name_RU
                }
                APP_ACTIVITY.sharedManager.getString(LANGUAGE_KEY) == LANGUAGE_DOT -> {
                    cartProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price_EURO)
                    product.name_DE
                }
                else -> {
                    cartProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price_USD)
                    product.name_BG
                }
            }

            if (product.productCount > 1) {
                productCountText.text = product.productCount.toString()
            }

//            cartProductPrice.text = APP_ACTIVITY.getString(R.string.current_carrency, product.price)
//            plusProductCount.setOnClickListener {}
//            minusProductCount.setOnClickListener {}

            deleteCartItem.setOnClickListener {
                listener.onClickDelete(product)
            }
        }
    }

    fun addItems(items: List<Basket>) {
        val insertPos = data.size
        data.addAll(items)
        notifyItemRangeInserted(insertPos, data.size)
    }

    fun deleteItem(item: Basket) {
        val position = data.indexOf(item)
        data.remove(item)
        notifyItemRemoved(position)
    }

    fun getProductById(id: Int): Basket {
        var result = Basket()
        for (item in data) {
            if (item.b_id == id) {
                result = item
            }
        }
        return result
    }

    fun setData(data: List<Basket>) {
        val insertPos = data.size
        this.data = data as ArrayList<Basket>
        notifyItemRangeInserted(insertPos, this.data.size)
    }

    fun setListener(listener: OnClickListener) {
        this.listener = listener
    }

    fun getData(): ArrayList<Basket> {
        return data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
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