package in.iejaz.orangequiz;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class devBottomSheet extends BottomSheetDialogFragment {

    private devSheetListener mListener;
    LinearLayout devDetails;
    LottieAnimationView fb, insta, github;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dev_bottomsheet, container, false);

       devDetails = view.findViewById(R.id.dev_details_layout);
        fb = view.findViewById(R.id.id2);
        insta = view.findViewById(R.id.id1);
        github = view.findViewById(R.id.id3);


//        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.slide_btm);
//        devDetails.setAnimation(animation);


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("fb");
                dismiss();
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("insta");
                dismiss();
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("github");
                dismiss();
            }
        });

//        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;
    }

    public interface devSheetListener{
        void onButtonClicked(String text);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (devSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+
                    "Must Implemt BpttomSheetListener");

        }

    }


}
