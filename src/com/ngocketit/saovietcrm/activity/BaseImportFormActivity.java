package com.ngocketit.saovietcrm.activity;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.ngocketit.saovietcrm.R;

public class BaseImportFormActivity extends LoggedInRequiredActivity implements OnClickListener {
	protected static final int FILE_CHOOSER = 0x1;

	Button btnImport;
	CheckBox chkDeleteOldItems;
	EditText txtFilePath; 
	ImageButton btnPickFile;
	ProgressDialog dlgProgress;
	ImportTask mImportTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_import_form);
		
		chkDeleteOldItems = (CheckBox)findViewById(R.id.chkDeleteOldItems);
		txtFilePath = (EditText)findViewById(R.id.txtFilePath);
		
		btnPickFile = (ImageButton)findViewById(R.id.btnPickFile);
		btnPickFile.setOnClickListener(this);

		btnImport = (Button)findViewById(R.id.btnImport);
		btnImport.setOnClickListener(this);
		
		int extraLayout = getViewExtraLayout();
		if (extraLayout > 0) {
			ViewGroup extraViewHolder = (ViewGroup)findViewById(R.id.extraLayout);
			LayoutInflater.from(this).inflate(extraLayout, extraViewHolder);
		}
	}
	
	protected int getViewExtraLayout() {
		return 0;
	}
	
	private void importFile(String filePath) {
		// For now, only support CSV
		importFromCSV(filePath);
	}
	
	private void importFromCSV(String filePath) {
		mImportTask = new ImportTask();

		if (chkDeleteOldItems.isChecked()) {
			mImportTask.mEmptyOldItems = true;
		}

		mImportTask.execute(filePath);
	}
	
	protected void onBeforeImport() {
		btnImport.setEnabled(false);
		btnPickFile.setEnabled(false);

		dlgProgress = ProgressDialog.show(this, getString(R.string.importing), getString(R.string.please_wait), true, true, new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				// Cancel import task when dialog is canceled
				if (mImportTask != null) {
					mImportTask.cancel(true);
				}
			}
		});
		dlgProgress.setCanceledOnTouchOutside(true);
	}
	
	protected void onPostImport(Integer result) {
		if (dlgProgress != null) {
			dlgProgress.dismiss();
		}

		btnImport.setEnabled(true);
		btnPickFile.setEnabled(true);
		
		Toast.makeText(this, String.valueOf(result) + " " + getString(R.string.items_imported), Toast.LENGTH_LONG).show();
	}
	
	protected void emptyOldItems() {
		
	}
	
	protected boolean importSingleItem(String[] items) {
		return false;
	}
	
	/**
	 * Do the import of items
	 * 
	 * @param entries A list of entry items
	 * @param task Import ask
	 * @return Number of success items
	 */
	protected Integer doImport(List<String[]> entries, ImportTask task) {
		int length = entries.size();
		int successCount = 0;
		
		for (int i = 0; i < length; i++) { 
			// Stop if task is cancelled by user
			if (task.isCancelled()) {
				break;
			}

			String[] items = entries.get(i);

			if (importSingleItem(items)) {
				successCount++;
			}
			
			task.setProgress(i);
		}
		
		return successCount;
	}
	
	protected void selectFile() {
		// Create the ACTION_GET_CONTENT Intent
		Intent getContentIntent = FileUtils.createGetContentIntent();

		Intent intent = Intent.createChooser(getContentIntent, getResources().getString(R.string.select_file));
		startActivityForResult(intent, FILE_CHOOSER);
	}

	@Override
	public void onClick(View v) {
		if (v == btnPickFile) {
			selectFile();
		}
		else if (v == btnImport) {
			if (TextUtils.isEmpty(txtFilePath.getText())) {
				Toast.makeText(this, getString(R.string.select_file_alert), Toast.LENGTH_LONG).show();
				txtFilePath.requestFocus();
				return;
			}

			if (chkDeleteOldItems.isChecked()) {
				new AlertDialog.Builder(this)
				.setTitle(R.string.delete_old_items)
				.setMessage(R.string.delete_old_items_warning)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						importFile(txtFilePath.getText().toString());
					}
				})
				.setNegativeButton(android.R.string.no, null)
				.show();
			} else {
				importFile(txtFilePath.getText().toString());
			}
		}
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FILE_CHOOSER && resultCode == RESULT_OK) {
			final Uri uri = data.getData();

			// Get the File path from the Uri
			String path = FileUtils.getPath(this, uri);

			// Alternatively, use FileUtils.getFile(Context, Uri)
			if (path != null && FileUtils.isLocal(path)) {
				txtFilePath.setText(path);
			}	
		}
	}
	
	protected class ImportTask extends AsyncTask<String, Integer, Integer> {
		public boolean mEmptyOldItems = false;
		
		public void setProgress(Integer progress) {
			publishProgress(progress);
		}
		
		@Override
		protected void onPreExecute() {
			// This one executes on UI thread
			super.onPreExecute();
			onBeforeImport();
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			onPostImport(result);
		}
		
		@Override
		protected void onCancelled(Integer result) {
			super.onCancelled();
			onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			int successCount = 0;
			
			try {
				// Empty old items if it's set
				if (mEmptyOldItems) {
					emptyOldItems();
				}

				int length = params.length;

				for(int i = 0; i < length; i++) {
					CSVReader reader = new CSVReader(new FileReader(params[i]));
					List<String[]> entries = reader.readAll();
					
					if (!entries.isEmpty()) {
						successCount += doImport(entries, this);
					}

					reader.close();
				}
			}
			catch (IOException e) {

			}

			return successCount;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
	}
}
