package com.example.authenticator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.authenticator.data.Account
import java.util.Timer
import java.util.TimerTask

class AccountAdapter(private var accounts: List<Account>) :
    RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val issuerText: TextView = view.findViewById(R.id.tv_issuer)
        val codeText: TextView = view.findViewById(R.id.tv_code)
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
        updateCode(holder.codeText, account.secret)

        // Обновление кода каждые 30 секунд
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateCode(holder.codeText, account.secret)
            }
        }, 0, 30_000)
    }

    private fun updateCode(textView: TextView, secret: String) {
        textView.post {
            textView.text = TotpGenerator.generateCode(secret)
        }
    }

    override fun getItemCount() = accounts.size
}