package com.tesis.yudith.showmethepast.view.ar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tesis.yudith.showmethepast.R;

import org.w3c.dom.Text;


public class DialogArInstructionsFragment extends DialogFragment {

    private static final String ARG_TITLE_ID = "ARG_TITLE_ID";
    private static final String ARG_INSTRUCTIONS_ID = "ARG_INSTRUCTIONS_ID";

    private String mTitle;
    private String mInstructions;

    private TextView txtTitle;
    private TextView txtInstructions;

    private Button btnOk;

    public DialogArInstructionsFragment() {

    }

    public static DialogArInstructionsFragment newInstance(String title, String instructions) {
        DialogArInstructionsFragment fragment = new DialogArInstructionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_ID, title);
        args.putString(ARG_INSTRUCTIONS_ID, instructions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mTitle = getArguments().getString(ARG_TITLE_ID);
            this.mInstructions = getArguments().getString(ARG_INSTRUCTIONS_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_ar_instructions, container, false);
        this.linkControls(view);
        return view;
    }

    private void linkControls(View view) {
        this.txtTitle = (TextView)view.findViewById(R.id.txt_dialogInstructions_title);
        this.txtInstructions = (TextView)view.findViewById(R.id.txt_dialogInstructions_instructions);
        this.btnOk = (Button)view.findViewById(R.id.btn_dialogInstructions_ok);

        this.txtTitle.setText(this.mTitle);
        this.txtInstructions.setText(this.mInstructions);



        this.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
