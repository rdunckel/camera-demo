package edu.wctc.android.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	private static final int MEDIA_TYPE_IMAGE = 1;
	protected static final String TAG = "TAG";
	private Camera camera;
	private CameraPreview cameraPreview;

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File pictureFile = Utils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (checkCameraHardware(this)) {
			// Create an instance of Camera
			camera = Utils.getCameraInstance();

			// Create our Preview view and set it as the content of our
			// activity.
			cameraPreview = new CameraPreview(this, camera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(cameraPreview);

			// Add a listener to the Capture button
			Button captureButton = (Button) findViewById(R.id.button_capture);
			captureButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an image from the camera
					camera.takePicture(null, null, mPicture);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Image saved to:\n" + data.getData(),
						Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

		if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Video captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Video saved to:\n" + data.getData(),
						Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the video capture
			} else {
				// Video capture failed, advise user
			}
		}

		camera.release();
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.release(); // release the camera for other applications
			camera = null;
		}
	}

}
