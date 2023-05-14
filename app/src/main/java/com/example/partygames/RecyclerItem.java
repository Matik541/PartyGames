package com.example.partygames;

public class RecyclerItem {
	private String content;
	private boolean checked = false;

	public RecyclerItem(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

}
