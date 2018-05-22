package com.project.linkto.fragment.message;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.project.linkto.R;
import com.project.linkto.bean.ChatMessage;
import com.project.linkto.bean.Person;
import com.project.linkto.bean.Userbd;
import com.project.linkto.fragment.BaseFragment;
import com.project.linkto.singleton.DataHelper;
import com.project.linkto.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import static com.project.linkto.BaseActivity.mDatabase;

/**
 * Created by bbouzaiene on 16/05/2018.
 */

public class ChatMessageFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private EditText ed_content_text;
    private Userbd userbd;
    private Button bt_submit;
    private Person mPerson;
    private String mUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        userbd = DataHelper.getInstance().getmUserbd();
        ed_content_text = (EditText) view.findViewById(R.id.ed_content_text);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        bt_submit = (Button) view.findViewById(R.id.bt_submit);
        drawViews();

        return view;

    }

    private void drawViews() {
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content_text = ed_content_text.getText().toString();
                if (!Utils.isEmptyString(content_text)) {
                    writeNewMessage(content_text);
                    ed_content_text.setText("");
                } else {
                    new MaterialDialog.Builder(mActivity)
                            .title(R.string.message)
                            .content(R.string.emptymessage)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void writeNewMessage(String content_text) {
        String key = mDatabase.child("messages").push().getKey();
        ChatMessage chatmessage = new ChatMessage(content_text, userbd.getUid());
        Map<String, Object> messagesValues = chatmessage.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> groupsUpdates = new HashMap<>();

        String key1 = mDatabase.child("messages").child(key).child("content").push().getKey();

        childUpdates.put(key1, messagesValues);

        mDatabase.child("messages").child(key).child("content").updateChildren(childUpdates);

        String groupkey1 = mDatabase.child("messages").child(key).child("groups").push().getKey();
        String groupkey2 = mDatabase.child("messages").child(key).child("groups").push().getKey();
        groupsUpdates.put(groupkey1, userbd.getUid());
        groupsUpdates.put(groupkey2, mUserId);
        mDatabase.child("messages").child(key).child("groups").updateChildren(groupsUpdates);
    }

    public void setmUser(Person mPerson) {
        this.mPerson = mPerson;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }
}
