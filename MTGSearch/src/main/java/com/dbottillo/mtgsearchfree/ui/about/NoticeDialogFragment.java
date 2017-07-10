package com.dbottillo.mtgsearchfree.ui.about;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.ui.BasicFragment;
import com.dbottillo.mtgsearchfree.util.LOG;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeDialogFragment extends BasicFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    @BindView(R.id.notice_dialog_title)
    TextView title;

    @BindView(R.id.notice_dialog_text)
    TextView text;

    public static DialogFragment newInstance(int title, int message) {
        NoticeDialogFragment instance = new NoticeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE, title);
        args.putInt(MESSAGE, message);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notice_dialog, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText(getArguments().getInt(TITLE));
        text.setText(getArguments().getInt(MESSAGE));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LOG.d();
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public String getPageTrack() {
        return "/notice_dialog";
    }

}
