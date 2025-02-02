package flutter.moum.open_appstore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** OpenAppstorePlugin */
public class OpenAppstorePlugin implements FlutterPlugin {
  /** Plugin registration. */

  private MethodChannel channel;

  public static void registerWith(Registrar registrar) {

    OpenAppstorePlugin plugin = new OpenAppstorePlugin();
    plugin.setMethodChannel(registrar.context(), registrar.messenger());
  }

  private void setMethodChannel(final Context context, BinaryMessenger messenger) {

    channel = new MethodChannel(messenger, "flutter.moum.open_appstore");
    channel.setMethodCallHandler(new MethodCallHandler() {
      @Override
      public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
          result.success("Android " + android.os.Build.VERSION.RELEASE);
        }
        else if (call.method.equals("openappstore")) {
          String android_id = call.argument("android_id");
          String manufacturer = android.os.Build.MANUFACTURER;
          if (manufacturer.equals("Amazon")) {
            launchActivity(context, "amzn://apps/android?p=" + android_id);
          } else {
            try {
              launchActivity(context, "market://details?id=" + android_id);
            } catch (android.content.ActivityNotFoundException e) {
              launchActivity(context, "https://play.google.com/store/apps/details?id=" + android_id);
            }
          }
          result.success("Android " + android.os.Build.VERSION.RELEASE);
        }
        else {
          result.notImplemented();
        }
      }
    });
  }

  public void launchActivity(Context context, String uri) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    setMethodChannel(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
