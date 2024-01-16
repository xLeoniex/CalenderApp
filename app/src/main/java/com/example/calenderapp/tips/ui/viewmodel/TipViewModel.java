package com.example.calenderapp.tips.ui.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.calenderapp.tips.model.TipModel;
import com.example.calenderapp.tips.source.TipRepository;
import com.example.calenderapp.tips.utils.ErrorMessages;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class TipViewModel extends AndroidViewModel {

    private TipRepository tipRepository;
    private ErrorMessages errorMessages;

    // Adding fields to bind
    public MutableLiveData<Integer> tipType_spinner_position = new MutableLiveData<>();

    public MutableLiveData<String> tipImageUrl = new MutableLiveData<>();
    public MutableLiveData<String> tipTitle = new MutableLiveData<>();
    public MutableLiveData<String> tipDescription = new MutableLiveData<>();
    public MutableLiveData<String> tipType = new MutableLiveData<>();

    public MutableLiveData<String> tipState = new MutableLiveData<>();
    public MutableLiveData<String> tipId = new MutableLiveData<>();
    private MutableLiveData<List<TipModel>> FetchedData = new MutableLiveData<>();
    private MutableLiveData<TipModel> tipModelMutableLiveData;

    public TipViewModel(@NonNull Application application) {
        super(application);
        tipRepository = new TipRepository();
        errorMessages = new ErrorMessages();
    }

    private TipModel CreateTip()
    {
        String tipIdValue = tipId.getValue()  !=null ? tipId.getValue():"";
        String tipStateValue  = tipState.getValue() !=null ? tipState.getValue():"inProgress";
        String tipTitleValue = tipTitle.getValue() != null ? tipTitle.getValue():"";
        String tipDescriptionValue = tipDescription.getValue() !=null ? tipDescription.getValue():"";
        String tipTypeValue = tipType.getValue() !=null ? tipType.getValue():"";
        String tipImageValue = tipImageUrl.getValue() !=null ? tipImageUrl.getValue():null;
        TipModel tipModel = new TipModel(tipTitleValue,tipIdValue,tipStateValue,tipDescriptionValue,tipTypeValue,tipImageValue);

        return tipModel;
    }

    public MutableLiveData<TipModel> getTipDetails()
    {
        if(tipModelMutableLiveData == null)
        {
            tipModelMutableLiveData = new MutableLiveData<>();
        }
        return tipModelMutableLiveData;
    }
    private void CreateTipOnMutableData(TipModel tipModel)
    {
        tipModelMutableLiveData.setValue(tipModel);
    }
    private void  PushTipToRepo(TipModel tipModel)
    {
        tipRepository.AddTipToDatabase(tipModel);
    }

    public String  OnClickHandler()
    {
        String msg;
        TipModel tipModel = CreateTip();
        CreateTipOnMutableData(tipModel);
        msg = InputValidationMsg(tipModel);
        if(msg.equals(errorMessages.getConfirmation_msg_Tip_Is_Pushed_To_Repository()))
        {
            PushTipToRepo(tipModel);
        }
        return msg;
    }
    public String InputValidationMsg(TipModel tipModel)
    {
        if(tipModel.getTipTitle().isEmpty() || tipModel.getTipType().equals("Tip Type"))
        {
            return errorMessages.getError_msg_No_Needed_Elements();
        }
        return errorMessages.getConfirmation_msg_Tip_Is_Pushed_To_Repository();
    }

    public MutableLiveData<List<TipModel>> getDataFromRepository()
    {
        FetchedData = tipRepository.getTipModelList();
        return FetchedData;

    }

    public MutableLiveData<Uri> uploadImageToRepo(Uri File)
    {
        return tipRepository.uploadTipImageToStorage(File);

    }

    public FirebaseUser getCurrentUser()
    {
        return tipRepository.getcurrentUser();
    }

}
