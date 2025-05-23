package com.example.authenticator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.authenticator.data.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccountAdapter(
    private var accounts: List<Account>,
    private val lifecycle: Lifecycle,
    private val onDeleteClick: (Account) -> Unit
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>(), DefaultLifecycleObserver {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val jobs = mutableMapOf<Int, Job>()

    init {
        lifecycle.addObserver(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val issuerText: TextView = view.findViewById(R.id.tv_issuer)
        val codeText: TextView = view.findViewById(R.id.tv_code)
        val btnDelete: Button = view.findViewById(R.id.btn_delete)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_time)
    }

    fun updateAccounts(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accounts[position]
        holder.issuerText.text = "${account.issuer}: ${account.username}"

        startCodeUpdates(holder.codeText,account.secret, holder.progressBar,  position)

        holder.btnDelete.setOnClickListener{
            onDeleteClick(account) }

    }

    private fun startCodeUpdates(textView: TextView,
                                 secret: String,
                                 progressBar: ProgressBar,
                                 position: Int) {
        jobs[position]?.cancel()
        jobs[position] = coroutineScope.launch {
            while (true) {
                textView.text = TotpGenerator.generateCode(secret)
//                repeat(TotpGenerator.TIME_STEP) {
                    progressBar.progress = TotpGenerator.getRemainingSeconds()
                    delay(1000)
//                delay(updateInterval)
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        val position = holder.absoluteAdapterPosition
        jobs.remove(position)?.cancel()
        super.onViewRecycled(holder)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        coroutineScope.cancel()
        lifecycle.removeObserver(this)
    }

    override fun getItemCount() = accounts.size
}