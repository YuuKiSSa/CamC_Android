package iss.workshop.adproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class CameraDetailDialogFragment extends DialogFragment {
    private static final String ARG_CAMERA_DETAIL = "cameraDetail";

    private CameraDetailDTO cameraDetail;

    public static CameraDetailDialogFragment newInstance(CameraDetailDTO cameraDetailDTO) {
        CameraDetailDialogFragment fragment = new CameraDetailDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CAMERA_DETAIL, cameraDetailDTO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cameraDetail = getArguments().getParcelable(ARG_CAMERA_DETAIL);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_detail_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 TextView
        TextView brandTextView = view.findViewById(R.id.dialog_brand);
        TextView modelTextView = view.findViewById(R.id.dialog_model);
        TextView categoryTextView = view.findViewById(R.id.dialog_category);
        TextView descriptionTextView = view.findViewById(R.id.dialog_description);
        TextView releaseTimeTextView = view.findViewById(R.id.dialog_releaseTime);
        TextView initialPriceTextView = view.findViewById(R.id.dialog_initialPrice);
        TextView effectivePixelTextView = view.findViewById(R.id.dialog_effectivePixel);
        TextView isoTextView = view.findViewById(R.id.dialog_iso);
        TextView focusPointTextView = view.findViewById(R.id.dialog_focusPoint);
        TextView continuousShotTextView = view.findViewById(R.id.dialog_continuousShot);
        TextView videoResolutionTextView = view.findViewById(R.id.dialog_videoResolution);
        TextView videoRateTextView = view.findViewById(R.id.dialog_videoRate);

        if (cameraDetail != null) {
            brandTextView.setText(cameraDetail.getBrand());
            modelTextView.setText(cameraDetail.getModel());
            categoryTextView.setText(cameraDetail.getCategory());
            descriptionTextView.setText(cameraDetail.getDescription());
            releaseTimeTextView.setText(cameraDetail.getReleaseTime() != null ? cameraDetail.getReleaseTime().toString() : "N/A");
            initialPriceTextView.setText(String.valueOf(cameraDetail.getInitialPrice()));
            effectivePixelTextView.setText(String.valueOf(cameraDetail.getEffectivePixel()));
            isoTextView.setText(String.valueOf(cameraDetail.getIso()));
            focusPointTextView.setText(cameraDetail.getFocusPoint() != null ? cameraDetail.getFocusPoint().toString() : "N/A");
            continuousShotTextView.setText(String.valueOf(cameraDetail.getContinuousShot()));
            videoResolutionTextView.setText(String.valueOf(cameraDetail.getVideoResolution()));
            videoRateTextView.setText(String.valueOf(cameraDetail.getVideoRate()));
        } else {
            // Handle the case when cameraDetail is null
            Log.e("CameraDetailDialogFragment", "CameraDetailDTO is null");
        }
    }
}
