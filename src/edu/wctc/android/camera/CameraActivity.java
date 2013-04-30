package edu.wctc.android.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private static final String TAG = "TAG";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

}