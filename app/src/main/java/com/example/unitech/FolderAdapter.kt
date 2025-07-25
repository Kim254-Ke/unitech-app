package com.example.unitech

// The following import statements enable the use of ThreeTenABP in my Adapter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(private val context: Context, private val newestPendingExamUnitsList: MutableList<String>) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.cardFolder)
        var folderName: TextView = itemView.findViewById(R.id.folderName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("FolderAdapter: ", "FolderAdapter")

        val stringedUnitName = newestPendingExamUnitsList[position]
        Log.d("stringedUnitName", "stringedUnitName: $stringedUnitName")
        val newStringedUnitName = stringedUnitName.replace("%%%%%SPACE%%%%%"," ")
            .replace("%%%%%COLON%%%%%", ":")
            .replace("%%%%%FOSTROKE%%%%%", "/")
            .replace("%%%%%BACKSLASH%%%%%", "\\")
            .replace("{", "%%%%%OPCURLY%%%%%")
            .replace("%%%%%CLOCURLY%%%%%", "}")
            .replace("%%%%%STAR%%%%%", "*")
            .replace("%%%%%QUESTIONMARK%%%%%", "?")
            .replace("%%%%%EXCLAMATIONMARK%%%%%", "!")
            .replace("%%%%%COMMA%%%%%", ",")
            .replace("%%%%%nextLine%%%%%", "\n")

        Log.d("newStringedUnitName", "newStringedUnitName: $newStringedUnitName")
        holder.folderName.text = newStringedUnitName
        val unitDetails = newestPendingExamUnitsList[position]

        holder.card.setOnClickListener {
            Toast.makeText(context, "pppppppppppppppppppppppppppp", Toast.LENGTH_LONG).show()
            Log.d("ppppppppppppppppp", "ppppppppppppppppp")

            val intent = Intent(context, FolderDetailsActivity::class.java)
            intent.putExtra("unitDetails", unitDetails)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return newestPendingExamUnitsList.size
    }

}