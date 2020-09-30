package com.example.yang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yang.myapplication.CurrentFriend;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.chat_contrue;
import com.example.yang.myapplication.sqlite_linkmanmss;
import com.example.yang.util.Contact;
import com.example.yang.util.Section;

import java.util.List;
import java.util.Locale;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private static final String NAME_FORMAT = "%s %s";

    private List<Contact> mContacts;
    private LayoutInflater mInflater;
    private ContactScrollerAdapter mContactScrollerAdapter;
    private Context mcontext;

    public ContactAdapter(Context c, List<Contact> contacts, ContactScrollerAdapter contactScrollerAdapter) {
        mcontext = c;
        mContacts = contacts;
        mInflater = LayoutInflater.from(c);
        mContactScrollerAdapter = contactScrollerAdapter;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contact = mInflater.inflate(R.layout.linkman_listview, parent, false);
        return new ContactViewHolder(contact);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        holder.pic.setImageDrawable(contact.getProfileImage());
        holder.mName.setText(String.format(Locale.US, NAME_FORMAT, contact.getFirstName(), contact.getLastName()));
        Section s = mContactScrollerAdapter.fromItemIndex(position);
        if (s != null) {
            holder.title.setText(s.getTitle());
        } else {
            holder.mview.setVisibility(View.VISIBLE);
            //holder.titlelinearlayout.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
        }
        holder.mfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_information = new Intent(mcontext, CurrentFriend.class);
                intent_information.putExtra(sqlite_linkmanmss.KEY_ACTNB, contact.getFirstName()+contact.getLastName());
                mcontext.startActivity(intent_information);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView pic;
        private TextView mName;
        private View mview;
        private LinearLayout titlelinearlayout;
        private LinearLayout mfriend;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mfriend = itemView.findViewById(R.id.linkman_listview_friend_linearlayout);
            titlelinearlayout = itemView.findViewById(R.id.linkman_listviewtitle_index_linearlayout);
            mview = itemView.findViewById(R.id.link_listview_divider);
            title = (TextView) itemView.findViewById(R.id.title_index);
            pic = (ImageView) itemView.findViewById(R.id.any_friend_image);
            mName = (TextView) itemView.findViewById(R.id.any_friend_textview);
        }
    }
}
