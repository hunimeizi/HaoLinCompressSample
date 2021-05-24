# HaoLin_Compress_Sample - 图片压缩，支持自定义配置

#### 使用方法
1.使用gradle
```js
repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation 'io.github.hunimeizi:haolinCompress:1.0.0'
}
```
2.使用配置
```js
   
   compressConfig = CompressConfig.builder()
                   .setUnCompressMinPixel(1000) // 最小像素不压缩，默认值：1000
                   .setUnCompressNormalPixel(2000) // 标准像素不压缩，默认值：2000
                   .setMaxPixel(1000) // 长或宽不超过的最大像素 (单位px)，默认值：1200
                   .setMaxSize(100 * 1024) // 压缩到的最大大小 (单位B)，默认值：200 * 1024 = 200KB
                   .enablePixelCompress(true) // 是否启用像素压缩，默认值：true
                   .enableQualityCompress(true) // 是否启用质量压缩，默认值：true
                   .enableReserveRaw(true) // 是否保留源文件，默认值：true
                   .setCacheDir("") // 压缩后缓存图片路径，默认值：Constants.COMPRESS_CACHE
                   .setShowCompressDialog(true) // 是否显示压缩进度条，默认值：false
                   .create();


```
3.实现CompressImage.CompressListener接口
```js
 @Override
    public void onCompressSuccess(ArrayList<Photo> images) {
        for (Photo image : images) {
            Log.e("lyb >>> ", "压缩成功 压缩后的路径为" + image.getCompressPath());
        }
        if (dialog != null && !isFinishing()) {
            dialog.dismiss();
        }
        Toast.makeText(MainActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompressFailed(ArrayList<Photo> images, String error) {
        Log.e("lyb >>> ", error);
        if (dialog != null && !isFinishing()) {
            dialog.dismiss();
        }
    }

    // 准备压缩，封装图片集合
    private void preCompress(String photoPath) {
        ArrayList<Photo> photos = new ArrayList<>();
        photos.add(new Photo(photoPath));
        if (!photos.isEmpty()) compress(photos);
    }

    // 开始压缩
    private void compress(ArrayList<Photo> photos) {
        if (compressConfig.isShowCompressDialog()) {
            Log.e("lyb >>> ", "开启了加载框");
            dialog = CommonUtils.showProgressDialog(this, "压缩中……");
        }
        CompressImageManager.build(this, compressConfig, photos, this).compress();
    }
```
4.如果开启了混淆 需要添加一下代码
```java
-keep class com.haolin.image.compress.library.bean.**{*;}
```

#### 内嵌上传 Maven Central
详细请看教程
[JCenter已经提桶跑路，是时候学会上传到Maven Central了](https://mp.weixin.qq.com/s/CrfYc1KsugJKPy_0rDZ49Q)