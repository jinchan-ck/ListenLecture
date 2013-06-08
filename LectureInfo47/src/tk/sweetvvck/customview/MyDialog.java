package tk.sweetvvck.customview;

import tk.sweetvvck.R;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

public class MyDialog extends AlertDialog {

    Context context;
    public MyDialog(Context context) {
        super(context);
        this.context = context;
    }
    public MyDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog);
    }
    
    public void setTheme(int theme){
    	
    }

}