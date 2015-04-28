package edu.ucsb.cs.cs185.justinliang.justinliangmultitouch;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {

	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(Environment.getExternalStorageDirectory().toString());
        
        File file = new File(Environment.getExternalStorageDirectory().toString()
        		+ File.separator + "ucsbmap.png");
        String filepath = Environment.getExternalStorageDirectory().toString() + File.separator + "ucsbmap.png";
        
        if(file.exists())
        {
	        TouchView touchView = (TouchView) findViewById(R.id.imageview);
	        touchView.setImageBitmap(BitmapFactory.decodeFile(filepath));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // When action bar menu is selected, perform these actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) 
    	{
	        case R.id.action_picture:
	        	pictureClick();
	            return true;
	        case R.id.action_settings:
	        	settingsDialog();
	            return true;
	        case R.id.action_help:
	        	helpDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    // Allow to select a picture
    public void pictureClick()
    {
    	// Create a new intent and start activity result
    	Intent intent = new Intent(); 
        intent.setType("image/*"); // Set intent type to image
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    
    // On activity result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData(); // get data and put it in URI selectedImage
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            // Set imageview to path of picture
            TouchView touchView = (TouchView) findViewById(R.id.imageview);
            touchView.setImageBitmap(BitmapFactory.decodeFile(picturePath));         
        }
    }
    
    // Settings dialog box that pops up when user selects the "settings" button
    public void settingsDialog()
    {
    	AlertDialog alert = new AlertDialog.Builder(this).create();
    	alert.setMessage("Haha no settings!");
    	alert.setButton("Done", new DialogInterface.OnClickListener()
    	{
    		@Override
    		public void onClick(DialogInterface dialog, int which)
    		{
    			dialog.dismiss();
    		}
    	});
    	alert.show();
    }
    
    // Help dialog box that pops up when user selects the "help" button
    public void helpDialog()
    {
    	AlertDialog alert = new AlertDialog.Builder(this).create();
    	alert.setMessage("Name: Justin Liang" + "\n" + "Version: 1.0");
    	alert.setButton("Done", new DialogInterface.OnClickListener()
    	{
    		@Override
    		public void onClick(DialogInterface dialog, int which)
    		{
    			dialog.dismiss();
    		}
    	});
    	alert.show();
    }
}
