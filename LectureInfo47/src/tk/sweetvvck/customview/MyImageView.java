package tk.sweetvvck.customview;

import java.io.Serializable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 自定义ImageView
 * 
 * @author sweetvvck
 * 
 */
public class MyImageView extends ImageView implements Serializable {

	 public MyImageView(Context context) {
		         super(context);
		 	        // TODO Auto-generated constructor stub
		 	    }
		 	 
		 	    public MyImageView(Context context, AttributeSet attrs,
		 	            int defStyle) {
		 	        super(context, attrs, defStyle);
		 	        // TODO Auto-generated constructor stub
		 	    }
		 	 
		 	    public MyImageView(Context context, AttributeSet attrs) {
		 	        super(context, attrs);
		 	        // TODO Auto-generated constructor stub
		     }

	private static final long serialVersionUID = 2040479387591628676L;

}
