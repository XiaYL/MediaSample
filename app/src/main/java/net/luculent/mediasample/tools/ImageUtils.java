package net.luculent.mediasample.tools;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * author xiayanlei
 */
public class ImageUtils {

    public static void displayImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}
