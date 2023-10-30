package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.adapter.ManageCompanyListAdapter.ManageCompanyHolder
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.util.ItemTouchHelper.ItemTouchHelperAdapter
import com.lusle.android.soon.util.ItemTouchHelper.ItemTouchHelperViewHolder
import java.util.*

class ManageCompanyListAdapter(private val mItemManageListener: OnItemManageListener) : BaseRecyclerAdapter<ManageCompanyHolder?>(), ItemTouchHelperAdapter {
    var list: ArrayList<Company>? = null

    inner class ManageCompanyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        var companyName: TextView = itemView.findViewById(R.id.companyName)
        override fun onItemSelected() {
            itemView.alpha = 0.7f
        }

        override fun onItemClear() {
            itemView.alpha = 1.0f
        }

        fun bind(company: Company) {
            companyName.text = company.name
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev = list!!.removeAt(fromPosition)
        list!!.add(toPosition, prev)
        notifyItemMoved(fromPosition, toPosition)
        mItemManageListener.onItemMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        val deletedItem = list!!.removeAt(position)
        notifyItemRemoved(position)
        mItemManageListener.onItemDismiss(deletedItem, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageCompanyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_company_manage, parent, false)
        return ManageCompanyHolder(view)
    }

    override fun onBindViewHolder(holder: ManageCompanyHolder, position: Int) {
        holder.bind(list!![position])
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    fun insertItem(item: Company, position: Int) {
        list!!.add(position, item)
        notifyItemInserted(position)
        mItemManageListener.insertItem(item, position)
    }

    interface OnItemManageListener {
        fun onDragStarted(viewHolder: RecyclerView.ViewHolder?)
        fun insertItem(item: Company, position: Int)
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(deletedItem: Company, position: Int)
    }
}