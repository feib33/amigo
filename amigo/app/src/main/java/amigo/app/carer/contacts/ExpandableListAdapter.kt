package amigo.app.carer

import amigo.app.R
import amigo.app.assisted.Listener
import amigo.app.assisted.VideoChat
import amigo.app.assisted.chatting
import amigo.app.auth.User
import amigo.app.carer.monitoring.newMonitoringIntent
import amigo.app.newBuildTripIntent
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView

class ExpandableListAdapter(private val context: Context,
                            data: ArrayList<User>)
    : BaseExpandableListAdapter() {

    var listData = data

    fun add(user: User) {
        listData.plus(user)
    }

    override fun getGroup(groupPosition: Int): User? {
        return listData[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {

        // get values from user at list position
        val headerTitle = (getGroup(groupPosition)?.firstname + " " + getGroup(groupPosition)?.lastname)
        val subTitle = if (getGroup(groupPosition)?.tripstatus ?: false) "Navigating" else "Idle"
        val status = getGroup(groupPosition)?.onlinestatus ?: false
        val profilePhoto = R.drawable.user

        // inflate the view
        val newConvertView = convertView
                ?: (this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.layout_contact_list_item, null)

        val listHeader = newConvertView.findViewById(R.id.name) as TextView
        val subtitleTextView = newConvertView.findViewById(R.id.location) as TextView
        val trayStatus = newConvertView.findViewById(R.id.status) as ImageView
        val profileImage = newConvertView.findViewById(R.id.profile_image) as ImageView
        val onlineStatus = newConvertView.findViewById(R.id.online) as ImageView

        // set values from user to inflated view
        listHeader.setTypeface(null, Typeface.BOLD)
        listHeader.text = headerTitle
        subtitleTextView.text = subTitle
        profileImage.setImageResource(profilePhoto)
        onlineStatus.setImageResource(if (status) R.drawable.status_green else R.drawable.status_grey)
        trayStatus.setImageResource(if (isExpanded) R.drawable.down_arrow else R.drawable.up_arrow)

        return newConvertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1;
    }

    override fun getChild(groupPosition: Int, childPosition: Int) {}

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong();
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val childView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.layout_assisted_person_list_item_child, null)
        val buildTripLink = childView.findViewById<TextView>(R.id.buildTripLink)
        val chatLink = childView.findViewById(R.id.chatButton) as ImageView
        val callLink = childView.findViewById(R.id.callButton) as ImageView
        val monitorLink = childView.findViewById<TextView>(R.id.monitorLink)

        callLink.setOnClickListener {
            context.startActivity(VideoChat.createIntent(context))
        }

        //when the user taps the link, automatically assign the uid to listener token.
        chatLink.setOnClickListener {
            Listener.Listenertoken = listData[groupPosition].uid
            Listener.ListenerName = getGroup(groupPosition)?.firstname ?: ""
            context.startActivity(chatting.createIntent(context))
        }

        //when the user taps the link, automatically assign the uid to listener token.
        buildTripLink.setOnClickListener {
            var newIntent = context.newBuildTripIntent()
            Listener.Listenertoken = listData[groupPosition].uid
            Listener.ListenerName = getGroup(groupPosition)?.firstname ?: ""
            newIntent.putExtra("userfirstname", (getGroup(groupPosition)?.firstname))
            newIntent.putExtra("uid", (getGroup(groupPosition)?.uid))
            context.startActivity(newIntent)
        }

        //when the user taps the link, automatically assign the uid to listener token.
        monitorLink.setOnClickListener {
            Listener.Listenertoken = listData[groupPosition].uid
            Listener.ListenerName = getGroup(groupPosition)?.firstname ?: ""
            var newIntent = context.newMonitoringIntent()
            newIntent.putExtra("userfirstname", (getGroup(groupPosition)?.firstname))
            newIntent.putExtra("uid", (getGroup(groupPosition)?.uid))
            context.startActivity(newIntent)
        }

        return childView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return this.listData.size
    }
}