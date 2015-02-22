package xk.crawler.dao;

public class BookShopBean {
	private String bookShopID;
	private String bookShopName;
	private String shopOwner;
	private String address;
	private String telephone;
	
	public BookShopBean(){}
	
	public BookShopBean(String bookShopID, String bookShopName, String shopOwner, String address, String telephone) {
		this.bookShopID = bookShopID;
		this.bookShopName = bookShopName;
		this.shopOwner = shopOwner;
		this.address = address;
		this.telephone = telephone;
	}
	public String getBookShopID() {
		return bookShopID;
	}
	public void setBookShopID(String bookShopID) {
		this.bookShopID = bookShopID;
	}
	public String getBookShopName() {
		return bookShopName;
	}
	public void setBookShopName(String bookShopName) {
		this.bookShopName = bookShopName;
	}
	public String getShopOwner() {
		return shopOwner;
	}
	public void setShopOwner(String shopOwner) {
		this.shopOwner = shopOwner;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
