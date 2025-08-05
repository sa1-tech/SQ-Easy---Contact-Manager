package com.sg.sq_easy;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	EditText nameInput, phoneInput, idInput, searchInput;
	Button addBtn, viewBtn, updateBtn, deleteBtn, searchBtn;
	TextView resultText;
	DBHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Bind views
		nameInput = findViewById(R.id.nameInput);
		phoneInput = findViewById(R.id.phoneInput);
		idInput = findViewById(R.id.idInput);
		resultText = findViewById(R.id.resultText);

		searchInput = findViewById(R.id.searchInput);
		searchBtn = findViewById(R.id.searchBtn);

		addBtn = findViewById(R.id.addBtn);
		viewBtn = findViewById(R.id.viewBtn);
		updateBtn = findViewById(R.id.updateBtn);
		deleteBtn = findViewById(R.id.deleteBtn);

		db = new DBHelper(this);

		refreshContactList(); // Auto-load on start

		// Add
		addBtn.setOnClickListener(v -> {
			String name = nameInput.getText().toString().trim();
			String phone = phoneInput.getText().toString().trim();
			if (name.isEmpty() || phone.isEmpty()) {
				toast("Please enter both name and phone");
				return;
			}
			boolean inserted = db.insertContact(name, phone);
			toast(inserted ? "Contact Added ✅" : "Failed to Add ❌");
			clearInputs();
			refreshContactList();
		});

		// View
		viewBtn.setOnClickListener(v -> refreshContactList());

		// Update
		updateBtn.setOnClickListener(v -> {
			String idStr = idInput.getText().toString().trim();
			String name = nameInput.getText().toString().trim();
			String phone = phoneInput.getText().toString().trim();
			if (idStr.isEmpty() || name.isEmpty() || phone.isEmpty()) {
				toast("Enter ID, Name & Phone");
				return;
			}
			int id = Integer.parseInt(idStr);
			boolean updated = db.updateContact(id, name, phone);
			toast(updated ? "Updated ✅" : "Update Failed ❌");
			clearInputs();
			refreshContactList();
		});

		// Delete
		deleteBtn.setOnClickListener(v -> {
			String idStr = idInput.getText().toString().trim();
			if (idStr.isEmpty()) {
				toast("Enter ID to delete");
				return;
			}
			int id = Integer.parseInt(idStr);
			boolean deleted = db.deleteContact(id);
			toast(deleted ? "Deleted 🗑️" : "Delete Failed ❌");
			clearInputs();
			refreshContactList();
		});

		// Search
		searchBtn.setOnClickListener(v -> {
			String keyword = searchInput.getText().toString().trim();
			if (keyword.isEmpty()) {
				toast("Enter keyword to search 🔍");
				return;
			}

			Cursor c = db.searchContacts(keyword);
			if (c.getCount() == 0) {
				resultText.setText("No matching contacts found.");
				return;
			}

			StringBuilder data = new StringBuilder();
			while (c.moveToNext()) {
				data.append("🆔 ID: ").append(c.getInt(0)).append("\n");
				data.append("👤 Name: ").append(c.getString(1)).append("\n");
				data.append("📞 Phone: ").append(c.getString(2)).append("\n\n");
			}
			resultText.setText(data.toString());
			c.close();
		});
	}

	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void clearInputs() {
		nameInput.setText("");
		phoneInput.setText("");
		idInput.setText("");
		searchInput.setText("");
	}

	private void refreshContactList() {
		Cursor c = db.getAllContacts();
		if (c.getCount() == 0) {
			resultText.setText("No contacts found.");
			return;
		}

		StringBuilder data = new StringBuilder();
		while (c.moveToNext()) {
			data.append("🆔 ID: ").append(c.getInt(0)).append("\n");
			data.append("👤 Name: ").append(c.getString(1)).append("\n");
			data.append("📞 Phone: ").append(c.getString(2)).append("\n\n");
		}
		resultText.setText(data.toString());
		c.close();
	}
}
