package com.yaya.mobile.category.bean;

public class CategoryEntity {
	public CategoryEntity(String imgUrl, int categoryId, int id, String title, String titleInParent) {
		super();
		this.imgUrl = imgUrl;
		this.categoryId = categoryId;
		this.id = id;
		this.title = title;
		this.titleInParent = titleInParent;
	}

	public String imgUrl;
	public int categoryId;
	public int id;
	public String title;
	public String titleInParent;

	@Override
	public String toString() {
		return "CategoryEntity [imgUrl=" + imgUrl + ", categoryId=" + categoryId + ", id=" + id + ", title=" + title + "]";
	}
}
