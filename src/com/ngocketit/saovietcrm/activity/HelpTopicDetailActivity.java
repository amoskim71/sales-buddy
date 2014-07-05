package com.ngocketit.saovietcrm.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;

public class HelpTopicDetailActivity extends LoggedInRequiredActivity {
	public static final String HELP_CONTENT = "help_content";
	public static final String HELP_TITLE = "help_title";
	
	TextView txtHelpContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_help_topic);
		
		txtHelpContent = (TextView)findViewById(R.id.txtHelpContent);
		Bundle extra = getIntent().getExtras();

		if (extra != null) {
			if (extra.containsKey(HELP_CONTENT)) {
				int contentResId = extra.getInt(HELP_CONTENT);
				String content = getString(contentResId);
				
				if (contentResId != R.string.help_general_content) {
					content += getString(R.string.csv_help);
				}

				txtHelpContent.setText(Html.fromHtml(content));
				txtHelpContent.setMovementMethod(LinkMovementMethod.getInstance());
			}
			
			if (extra.containsKey(HELP_TITLE)) {
				setTitle(extra.getInt(HELP_TITLE));
			}
		}
	}
}
