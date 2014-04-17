package tk.sweetvvck.utils;

import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.graphics.Canvas;  
import android.graphics.LinearGradient;  
import android.graphics.Matrix;  
import android.graphics.Paint;  
import android.graphics.PorterDuffXfermode;  
import android.graphics.Bitmap.Config;  
import android.graphics.PorterDuff.Mode;  
import android.graphics.Shader.TileMode;  
import android.graphics.drawable.BitmapDrawable;  
import android.util.AttributeSet;  
import android.widget.ImageView;  
  
/**
 * 自定义ImageView，实现图片倒影效果
 * @author sweetvvck
 *
 */
public class ReflectImageView extends ImageView {  
  
    private Bitmap originalBitmap;  
  
    public ReflectImageView(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
    }  
  
    public ReflectImageView(Context context) {  
        this(context, null, 0);  
    }  
  
    public ReflectImageView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        DoReflection(((BitmapDrawable) getDrawable()).getBitmap());  
    }  
  
    @Override  
    public void setImageBitmap(Bitmap bm) {  
        DoReflection(bm);  
  
    }  
  
    /** 显示倒影效果的setImageBitmap函数 */  
    public void setImageBitmap(Bitmap bm, boolean isFlected) {  
        if (isFlected) {  
            super.setImageBitmap(bm);  
        }  
    }  
  
    @Override  
    public void setImageResource(int resId) {  
  
        originalBitmap = BitmapFactory.decodeResource(getResources(), resId);  
        DoReflection(originalBitmap);  
    }  
  
    private void DoReflection(Bitmap bitmap) {  
        final int reflectionGap = 0;  
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        Matrix matrix = new Matrix();  
        matrix.preScale(1, -1);  
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,  
                width, height / 2, matrix, false);  
        Bitmap bitmap4Reflection = Bitmap.createBitmap(width,  
                (height + height / 2), Config.ARGB_8888);  
        Canvas canvasRef = new Canvas(bitmap4Reflection);  
        Paint deafaultPaint = new Paint();  
        deafaultPaint.setAntiAlias(true);  
        canvasRef.drawBitmap(bitmap, 0, 0, null);  
        canvasRef.drawRect(0, height, width, height + reflectionGap,  
                deafaultPaint);  
        canvasRef.drawBitmap(reflectionImage, 0, height + reflectionGap, null);  
        Paint paint = new Paint();  
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,  
                bitmap4Reflection.getHeight() + reflectionGap, 0x70ffffff,  
                0x00ffffff, TileMode.CLAMP);  
        paint.setShader(shader);  
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
        canvasRef.drawRect(0, height, width, bitmap4Reflection.getHeight()  
                + reflectionGap, paint);  
        this.setImageBitmap(bitmap4Reflection, true);  
    }  
}