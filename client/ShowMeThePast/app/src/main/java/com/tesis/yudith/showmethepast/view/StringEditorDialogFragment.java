package com.tesis.yudith.showmethepast.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;

public class StringEditorDialogFragment extends DialogFragment {
    public interface IStringEditorListener {
        void onChange(StringEditorDialogFragment target, int targetField, int targetIndex, MultiLanguageString values);
    }

    private static final String ARG_TARGET_INDEX = "ARG_TARGET_INDEX";
    private static final String ARG_TARGET_FIELD = "TARGET_FIELD_TITLE";
    private static final String ARG_DIALOG_TITLE = "DIALOG_TITLE";

    private static final String ARG_ENGLISH_CONTENT = "ENGLISH_CONTENT";
    private static final String ARG_SPANISH_CONTENT = "SPANISH_CONTENT";

    private String dialogTitle;

    private String englishContent;
    private String spanishContent;
    private int targetField;
    private int targetIndex;

    private String initialEnglishContent;
    private String initialSpanishContent;


    private EditText txtSpanish;
    private EditText txtEnglish;

    IStringEditorListener listener;

    public StringEditorDialogFragment() {

    }

    public void setListener(IStringEditorListener listener) {
        this.listener = listener;
    }

    public static StringEditorDialogFragment newInstance(String dialogTitle, IStringEditorListener listener, int targetField, int targetIndex, MultiLanguageString values) {
        StringEditorDialogFragment fragment = new StringEditorDialogFragment();
        fragment.setListener(listener);

        Bundle args = new Bundle();

        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putString(ARG_ENGLISH_CONTENT, values.getEnglish());
        args.putString(ARG_SPANISH_CONTENT, values.getSpanish());

        args.putInt(ARG_TARGET_FIELD, targetField);
        args.putInt(ARG_TARGET_INDEX, targetIndex);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //int title = getArguments().getInt("title");

        final StringEditorDialogFragment self = this;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_string_editor_dialog, null);

        this.txtEnglish = (EditText) view.findViewById(R.id.txt_StringEditorDialog_English);
        this.txtSpanish = (EditText) view.findViewById(R.id.txt_StringEditorDialog_Spanish);

        this.txtEnglish.setText(this.englishContent);
        this.txtSpanish.setText(this.spanishContent);

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_edit_black_24dp)
                .setTitle(this.dialogTitle)
                .setView(view)
                .setPositiveButton(R.string.label_alert_dialog_ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        self.englishContent = self.txtEnglish.getText().toString();
                        self.spanishContent = self.txtSpanish.getText().toString();

                        if (self.hasChanges() && self.listener != null) {
                            self.listener.onChange(self, self.targetField, self.targetIndex, new MultiLanguageString(self.englishContent, self.spanishContent));
                        }
                        }
                    }
                )
                .setNegativeButton(R.string.label_alert_dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        //((FragmentAlertDialog)getActivity()).doNegativeClick();
                        }
                    }
                )
                .create();
    }

    private boolean hasChanges() {

        if (this.initialEnglishContent.compareTo(this.englishContent) != 0) {
            return true;
        }

        if (this.initialSpanishContent.compareTo(this.spanishContent) != 0) {
            return true;
        }

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.dialogTitle = getArguments().getString(ARG_DIALOG_TITLE);
            this.initialEnglishContent = this.englishContent = getArguments().getString(ARG_ENGLISH_CONTENT);
            this.initialSpanishContent = this.spanishContent = getArguments().getString(ARG_SPANISH_CONTENT);
            this.targetField = getArguments().getInt(ARG_TARGET_FIELD);
            this.targetIndex = getArguments().getInt(ARG_TARGET_INDEX);

            if (this.initialEnglishContent == null) {
                this.englishContent = "";
                this.initialEnglishContent = "";
            }

            if (this.initialSpanishContent == null) {
                this.spanishContent = "";
                this.initialSpanishContent = "";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_string_editor_dialog, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
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
