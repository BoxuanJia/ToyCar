package com.github.boxuanjia.toycar;

import com.github.boxuanjia.rxharmonylife.RxLife;
import com.github.boxuanjia.toycar.cache.DiskCache;
import com.github.boxuanjia.toycar.cache.DiskLruCacheWrapper;
import com.github.boxuanjia.toycar.log.ToyLog;
import com.github.boxuanjia.toycar.util.Util;
import io.reactivex.Observable;
import io.reactivex.harmony.schedulers.HarmonySchedulers;
import io.reactivex.schedulers.Schedulers;
import ohos.agp.components.Image;
import ohos.app.Context;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ToyCar {

    private static DiskCache diskCache;

    private static OkHttpClient client;

    public static void initialize(Context context) {
        diskCache = DiskLruCacheWrapper.get(getPhotoCacheDir(context, "image_manager_disk_cache"), 250 * 1024 * 1024);
        client = new OkHttpClient();
        ToyLog.initialize();
    }

    public static Request load(String string) {
        return new Request(string);
    }

    public static class Request {

        private String url;

        public Request(String url) {
            this.url = url;
        }

        public void into(Image image) {
            PixelMap pixelMap;
            InputStream is = diskCache.get(generateSafeKey(url));
            if (is != null) {
                ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
                srcOpts.formatHint = "image/jpg";
                ImageSource imageSource = ImageSource.create(is, srcOpts);
                if (imageSource == null) {
                    return;
                }
                pixelMap = imageSource.createPixelmap(null);
                if (pixelMap == null) {
                    diskCache.delete(generateSafeKey(url)); //the image must have been corrupted
                } else {
                    image.setPixelMap(pixelMap);
                    return;
                }
            }
            Observable.just(url)
                    .subscribeOn(Schedulers.io())
                    .map(s -> {
                        okhttp3.Request request = new okhttp3.Request.Builder()
                                .url(s)
                                .build();
                        return client.newCall(request).execute().body();
                    })
                    .observeOn(HarmonySchedulers.mainThread())
                    .as(RxLife.as(image))
                    .subscribe(responseBody -> {
                        InputStream inputStream = responseBody.byteStream();
                        ImageSource imageSource = ImageSource.create(inputStream, new ImageSource.SourceOptions());
                        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
                        decodingOptions.desiredPixelFormat = PixelFormat.ARGB_8888;
                        PixelMap pixelMap1 = imageSource.createPixelmap(decodingOptions);
                        image.setPixelMap(pixelMap1);
                        diskCache.put(generateSafeKey(url), os -> {
                            ImagePacker imagePacker = ImagePacker.create();
                            ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
                            imagePacker.initializePacking(os, packingOptions);
                            imagePacker.addImage(pixelMap1);
                            imagePacker.finalizePacking();
                        });
                    }, throwable -> ToyLog.e("Unable to download picture"));
        }
    }

    private static File getPhotoCacheDir(Context context, String cacheName) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            result.mkdirs();
            return result;
        }
        return null;
    }

    private static String generateSafeKey(String url) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(url.getBytes("UTF-8"));
            return Util.sha256BytesToHex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
